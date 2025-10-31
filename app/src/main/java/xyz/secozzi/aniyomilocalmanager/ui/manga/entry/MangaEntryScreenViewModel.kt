package xyz.secozzi.aniyomilocalmanager.ui.manga.entry

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.asResultFlow

class MangaEntryScreenViewModel(
    private val path: String,
    private val trackerRepository: MangaTrackerRepository,
    private val storageManager: StorageManager,
    private val json: Json,
) : ViewModel() {

    val state = trackerRepository.getTrackData(path)
        .distinctUntilChanged()
        .asResultFlow(
            idleResult = State.Idle,
            loadingResult = State.Idle,
            getErrorResult = { State.Error(it) },
        ) { data ->
            val entity = data ?: MangaTrackerEntity(path)

            val dir = storageManager.getFromPath(path)!!
            val names = dir.children
                .filterNot { it.fullName.startsWith(".") }
                .map { it.fullName }

            State.Success(
                name = dir.fullName,
                hasCover = names.any { it.contains("cover") },
                hasComicInfo = names.any { it.equals("comicinfo.xml", true) },
                data = entity,
            )
        }

    fun updateIds(resultData: String) {
        val result = json.decodeFromString<SearchResultItem>(resultData)
        viewModelScope.launch {
            trackerRepository.upsert(
                MangaTrackerEntity(
                    path = path,
                    mangabaka = result.trackerIds[TrackerIds.MangaBaka],
                    anilist = result.trackerIds[TrackerIds.AniList],
                    mal = result.trackerIds[TrackerIds.Mal],
                ),
            )
        }
    }

    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val exception: Throwable) : State

        @Immutable
        data class Success(
            val name: String,
            val hasCover: Boolean,
            val hasComicInfo: Boolean,
            val data: MangaTrackerEntity,
        ) : State
    }
}
