package xyz.secozzi.aniyomilocalmanager.ui.manga.entry

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel

class MangaEntryScreenViewModel(
    private val path: String,
    private val repository: MangaTrackerRepository,
    private val storageManager: StorageManager,
) : StateViewModel<MangaEntryScreenViewModel.State>(State.Idle) {

    init {
        viewModelScope.launch {
            repository.getTrackData(path).collectLatest { data ->
                val entity = data ?: MangaTrackerEntity(path)

                val dir = storageManager.getFromPath(path)!!
                val names = dir.children.map { it.fullName }

                mutableState.update { _ ->
                    State.Success(
                        name = dir.fullName,
                        hasCover = names.any { it.contains("cover") },
                        hasComicInfo = names.any { it.equals("comicinfo.xml", true) },
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
            val hasComicInfo: Boolean,
            val data: MangaTrackerEntity,
        ) : State
    }
}
