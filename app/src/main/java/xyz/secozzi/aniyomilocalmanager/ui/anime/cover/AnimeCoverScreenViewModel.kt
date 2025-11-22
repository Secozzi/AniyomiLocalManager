package xyz.secozzi.aniyomilocalmanager.ui.anime.cover

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.baseName
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.CoverRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchResult
import xyz.secozzi.aniyomilocalmanager.utils.asResultFlow

class AnimeCoverScreenViewModel(
    private val path: String,
    private val coverPreferences: CoverPreferences,
    private val trackerRepository: AnimeTrackerRepository,
    private val coverRepository: CoverRepository,
    private val storageManager: StorageManager,
    private val client: HttpClient,
) : ViewModel() {
    val name = flow { emit(path) }
        .asResultFlow(
            idleResult = "",
            loadingResult = "",
            getErrorResult = { "" },
            dispatcher = Dispatchers.IO,
        ) { path ->
            storageManager.getFromPath(path)!!.fullName
        }

    fun updateIds(result: SearchResult) {
        viewModelScope.launch {
            trackerRepository.upsert(
                AnimeTrackerEntity(
                    path = path,
                    anilist = result[TrackerIds.Anilist],
                    mal = result[TrackerIds.Mal],
                    anidb = result[TrackerIds.Anidb],
                ),
            )
        }
    }

    private val _toastEvent = MutableSharedFlow<ToastEvent>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _isDownloadingCover = MutableStateFlow(false)
    val isDownloadingCover = _isDownloadingCover.asStateFlow()

    private val _selectedCover = MutableStateFlow<CoverData?>(null)
    val selectedCover = _selectedCover.asStateFlow()

    private val preferencesFlow = combine(
        coverPreferences.animeCoverAnilist.stateIn(viewModelScope),
        coverPreferences.animeCoverMal.stateIn(viewModelScope),
        coverPreferences.animeCoverFanart.stateIn(viewModelScope),
    ) { al, mal, fa -> Triple(al, mal, fa) }

    val state = combine(
        trackerRepository.getTrackData(path),
        preferencesFlow,
    ) { data, preferences -> data to preferences }
        .distinctUntilChanged()
        .asResultFlow(
            idleResult = State.Idle,
            loadingResult = State.Loading,
            getErrorResult = { State.Error(it) },
            dispatcher = Dispatchers.IO,
        ) { (data, prefs) ->
            if (data?.anilist == null && data?.mal == null) {
                State.NoID
            } else {
                _selectedCover.update { _ -> null }
                val covers = coverRepository.getAnimeCovers(data, prefs.first, prefs.second, prefs.third)

                State.Success(
                    data = covers.toPersistentList(),
                )
            }
        }

    private suspend fun performDownload(coverData: CoverData): Boolean {
        val fileExt = coverData.coverUrl.split(".").lastOrNull() ?: "jpg"
        val dir = storageManager.getFromPath(path) ?: return false

        // Rename previous covers
        dir.children.forEach {
            if (it.baseName.equals("cover", true)) {
                it.renameTo(it.fullName + ".old")
            }
        }

        val cover = dir.createFile("image/$fileExt", "cover.$fileExt")
            ?: return false

        withContext(Dispatchers.IO) {
            storageManager.getOutputStream(cover, "wt").use { output ->
                client.get(coverData.coverUrl).bodyAsChannel().toInputStream().use { input ->
                    input.copyTo(output!!)
                }
            }
        }

        return true
    }

    fun downloadCover(coverData: CoverData) {
        if (isDownloadingCover.value) return

        viewModelScope.launch {
            _isDownloadingCover.update { _ -> true }

            val result = performDownload(coverData)
            _toastEvent.emit(ToastEvent.Downloaded(result))

            _isDownloadingCover.update { _ -> false }
        }
    }

    fun selectCover(coverData: CoverData) {
        if (selectedCover.value == coverData) {
            _selectedCover.update { _ -> null }
        } else {
            _selectedCover.update { _ -> coverData }
        }
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Loading : State

        @Immutable
        data class Error(val exception: Throwable) : State

        @Immutable
        data object NoID : State

        @Immutable
        data class Success(
            val data: ImmutableList<CoverData>,
        ) : State
    }

    @Immutable
    sealed interface ToastEvent {
        @Immutable
        data class Downloaded(val success: Boolean) : ToastEvent
    }
}
