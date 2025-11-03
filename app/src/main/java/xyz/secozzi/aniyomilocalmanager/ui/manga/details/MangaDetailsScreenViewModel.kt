package xyz.secozzi.aniyomilocalmanager.ui.manga.details

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.domain.entry.manga.model.ComicInfo
import xyz.secozzi.aniyomilocalmanager.domain.entry.manga.model.ComicInfoPublishingStatus
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel
import xyz.secozzi.aniyomilocalmanager.utils.withMinimumDuration
import kotlin.time.Duration.Companion.seconds

class MangaDetailsScreenViewModel(
    private val path: String,
    private val trackerRepository: MangaTrackerRepository,
    private val storageManager: StorageManager,
    private val searchManager: SearchManager,
    private val xml: XML,
) : StateViewModel<MangaDetailsScreenViewModel.State>(State.Idle) {

    init {
        viewModelScope.launch {
            trackerRepository.getTrackData(path).collectLatest { data ->
                val searchIds = buildMap(3) {
                    if (data?.mangabaka != null) {
                        put(SearchIds.MangaBaka, data.mangabaka.toString())
                    }
                    if (data?.anilist != null) {
                        put(SearchIds.AnilistManga, data.anilist.toString())
                    }
                    if (data?.mal != null) {
                        put(SearchIds.MalManga, data.mal.toString())
                    }
                }.toPersistentMap()
                val details = getDetailsFromComicinfo(path)

                mutableState.update {
                    State.Success(
                        searchIds = searchIds,
                        details = details,
                    )
                }
            }
        }
    }

    private fun getDetailsFromComicinfo(path: String): EntryDetails {
        return storageManager.getFromPath(path)?.findFile("ComicInfo.xml")?.let {
            try {
                val data = storageManager.getInputStream(it)!!.reader().use { r ->
                    xml.decodeFromString<ComicInfo>(r.readText())
                }

                EntryDetails(
                    title = data.series?.value.orEmpty(),
                    titles = listOfNotNull(data.series?.value),
                    authors = data.writer?.value.orEmpty(),
                    artists = data.penciller?.value.orEmpty(),
                    description = data.summary?.value.orEmpty(),
                    genre = data.genre?.value.orEmpty(),
                    status = ComicInfoPublishingStatus.toStatusValue(data.publishingStatus?.value),
                )
            } catch (e: Exception) {
                Log.i("aniyomi-local-manager", e.stackTraceToString())
                EntryDetails.Empty
            }
        } ?: EntryDetails.Empty
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedSearch = MutableStateFlow<SearchIds?>(null)
    val selectedSearch = _selectedSearch.asStateFlow()

    fun updateSelectedSearchId(selected: SearchIds?) {
        _selectedSearch.update { _ -> selected }
        val successState = state.value as? State.Success ?: return

        _isLoading.update { _ -> true }

        viewModelScope.launch(Dispatchers.IO) {
            val details = if (selected == null) {
                getDetailsFromComicinfo(path)
            } else {
                val id = successState.searchIds[selected]!!
                withMinimumDuration(1.5.seconds) {
                    searchManager.getSearchRepository(selected).getFromId(id)
                }
            }

            mutableState.update { _ ->
                successState.copy(
                    details = details,
                )
            }

            _isLoading.update { _ -> false }
        }
    }

    fun updateTitle(title: String) {
        updateDetails { it.copy(title = title) }
    }

    fun updateAuthor(author: String) {
        updateDetails { it.copy(authors = author) }
    }

    fun updateArtist(artist: String) {
        updateDetails { it.copy(artists = artist) }
    }

    fun updateDescription(description: String) {
        updateDetails { it.copy(description = description) }
    }

    fun updateGenre(genre: String) {
        updateDetails { it.copy(genre = genre) }
    }

    fun updateStatus(status: Status) {
        updateDetails { it.copy(status = status) }
    }

    private inline fun updateDetails(f: (EntryDetails) -> EntryDetails) {
        mutableState.update {
            when (it) {
                is State.Success -> it.copy(
                    details = f(it.details),
                )
                else -> it
            }
        }
    }

    fun generateXmlString(): String? {
        val details = (state.value as? State.Success)?.details ?: return null

        val comicInfo = ComicInfo(
            series = ComicInfo.Series(details.title),
            summary = details.description.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Summary(it)
            },
            writer = details.authors.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Writer(it)
            },
            penciller = details.artists.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Penciller(it)
            },
            genre = details.genre.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Genre(it)
            },
            publishingStatus = ComicInfo.PublishingStatusTachiyomi(
                ComicInfoPublishingStatus.toComicInfoValue(details.status),
            ),
        )

        return xml.encodeToString(ComicInfo.serializer(), comicInfo)
    }

    private fun performDownload(): Boolean {
        val data = generateXmlString() ?: return false
        val dir = storageManager.getFromPath(path) ?: return false
        val comicInfo = dir.findFile("ComicInfo.xml")
            ?: dir.createFile("application/xml", "ComicInfo.xml")
            ?: return false

        storageManager.getOutputStream(comicInfo, "wt").use { output ->
            output!!.write(data.toByteArray())
        }

        return true
    }

    fun copyComicInfo() {
        viewModelScope.launch {
            val data = generateXmlString()
            if (data != null) {
                _uiEvent.emit(UiEvent.Copy(data))
            }
        }
    }

    fun generateComicInfo() {
        viewModelScope.launch {
            val result = performDownload()
            _uiEvent.emit(UiEvent.Downloaded(result))
        }
    }

    @Immutable
    sealed interface UiEvent {
        @Immutable
        data class Downloaded(val success: Boolean) : UiEvent

        @Immutable
        data class Copy(val text: String) : UiEvent
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val throwable: Throwable) : State

        @Immutable
        data class Success(
            val searchIds: ImmutableMap<SearchIds, String>,
            val details: EntryDetails,
        ) : State
    }
}
