package xyz.secozzi.aniyomilocalmanager.ui.manga.entry

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.COMIC_INFO_FILE
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.asResultFlow

class MangaEntryScreenViewModel(
    private val path: String,
    private val trackerRepository: MangaTrackerRepository,
    private val storageManager: StorageManager,
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

    val detailsState = flow { emit(path) }
        .asResultFlow(
            idleResult = null,
            loadingResult = null,
            getErrorResult = { null },
            dispatcher = Dispatchers.IO,
        ) { path ->
            val dir = storageManager.getFromPath(path)!!
            val names = dir.children
                .filterNot { it.fullName.startsWith(".") }
                .map { it.fullName }

            DetailsInfo(
                hasCover = names.any { it.startsWith("cover", true) },
                hasComicInfo = names.any { it.equals(COMIC_INFO_FILE, true) },
            )
        }

    val state = trackerRepository.getTrackData(path)
        .distinctUntilChanged()
        .asResultFlow(
            idleResult = State.Idle,
            loadingResult = State.Idle,
            getErrorResult = { State.Error(it) },
            dispatcher = Dispatchers.IO,
        ) { data ->
            val entity = data ?: MangaTrackerEntity(path)

            State.Success(
                data = entity,
            )
        }

    fun updateIds(result: SearchResultItem) {
        viewModelScope.launch {
            trackerRepository.upsert(
                MangaTrackerEntity(
                    path = path,
                    mangabaka = result.trackerIds[TrackerIds.MangaBaka],
                    anilist = result.trackerIds[TrackerIds.Anilist],
                    mal = result.trackerIds[TrackerIds.Mal],
                ),
            )
        }
    }

    @Stable
    data class DetailsInfo(
        val hasCover: Boolean,
        val hasComicInfo: Boolean,
    )

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val exception: Throwable) : State

        @Immutable
        data class Success(
            val data: MangaTrackerEntity,
        ) : State
    }
}
