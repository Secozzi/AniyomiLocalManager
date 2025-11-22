package xyz.secozzi.aniyomilocalmanager.ui.anime.entry

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.DETAILS_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODES_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.asResultFlow
import kotlin.time.Duration.Companion.seconds

class AnimeEntryScreenViewModel(
    private val path: String,
    private val trackerRepository: AnimeTrackerRepository,
    private val storageManager: StorageManager,
) : ViewModel() {

    val name = flow { emit(storageManager.getFromPath(path)!!.fullName) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = "",
        )

    val state = trackerRepository.getTrackData(path)
        .distinctUntilChanged()
        .asResultFlow(
            idleResult = State.Idle,
            loadingResult = State.Idle,
            getErrorResult = { State.Error(it) },
            dispatcher = Dispatchers.IO,
        ) { data ->
            val entity = data ?: AnimeTrackerEntity(path)

            val dir = storageManager.getFromPath(path)!!
            val names = dir.children
                .filterNot { it.fullName.startsWith(".") }
                .map { it.fullName }

            State.Success(
                hasCover = names.any { it.startsWith("cover", true) },
                hasDetails = names.any { it == DETAILS_JSON },
                hasEpisodes = names.any { it == EPISODES_JSON },
                data = entity,
            )
        }

    fun updateIds(result: SearchResultItem) {
        viewModelScope.launch {
            trackerRepository.upsert(
                AnimeTrackerEntity(
                    path = path,
                    anilist = result.trackerIds[TrackerIds.Anilist],
                    mal = result.trackerIds[TrackerIds.Mal],
                    anidb = result.trackerIds[TrackerIds.Anidb],
                ),
            )
        }
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val exception: Throwable) : State

        @Immutable
        data class Success(
            val hasCover: Boolean,
            val hasDetails: Boolean,
            val hasEpisodes: Boolean,
            val data: AnimeTrackerEntity,
        ) : State
    }
}
