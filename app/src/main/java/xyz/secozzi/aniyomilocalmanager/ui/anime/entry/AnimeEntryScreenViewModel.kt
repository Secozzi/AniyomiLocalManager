package xyz.secozzi.aniyomilocalmanager.ui.anime.entry

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel

class AnimeEntryScreenViewModel(
    private val path: String,
    private val repository: AnimeTrackerRepository,
    private val storageManager: StorageManager,
) : StateViewModel<AnimeEntryScreenViewModel.State>(State.Idle) {

    init {
        viewModelScope.launch {
            repository.getTrackData(path).collectLatest { data ->
                val entity = data ?: AnimeTrackerEntity(path)

                val dir = storageManager.getFromPath(path)!!
                val names = dir.children
                    .filterNot { it.fullName.startsWith(".") }
                    .map { it.fullName }

                mutableState.update { _ ->
                    State.Success(
                        name = dir.fullName,
                        hasCover = names.any { it.contains("cover") },
                        hasDetails = names.any { it == "details.json" },
                        hasEpisodes = names.any { it == "episodes.json" },
                        data = entity,
                    )
                }
            }
        }
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Success(
            val name: String,
            val hasCover: Boolean,
            val hasDetails: Boolean,
            val hasEpisodes: Boolean,
            val data: AnimeTrackerEntity,
        ) : State
    }
}
