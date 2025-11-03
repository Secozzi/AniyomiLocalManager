package xyz.secozzi.aniyomilocalmanager.ui.anime.details

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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.AnimeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel
import xyz.secozzi.aniyomilocalmanager.utils.withMinimumDuration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSerializationApi::class)
class AnimeDetailsScreenViewModel(
    private val path: String,
    private val trackerRepository: AnimeTrackerRepository,
    private val storageManager: StorageManager,
    private val searchManager: SearchManager,
    private val json: Json,
) : StateViewModel<AnimeDetailsScreenViewModel.State>(State.Idle) {

    init {
        viewModelScope.launch {
            trackerRepository.getTrackData(path).collectLatest { data ->
                val searchIds = buildMap(2) {
                    if (data?.anilist != null) {
                        put(SearchIds.AnilistAnime, data.anilist.toString())
                    }
                    if (data?.mal != null) {
                        put(SearchIds.MalAnime, data.mal.toString())
                    }
                }.toPersistentMap()
                val details = getDetailsFromDetails(path)

                mutableState.update {
                    State.Success(
                        searchIds = searchIds,
                        details = details,
                    )
                }
            }
        }
    }

    private fun getDetailsFromDetails(path: String): EntryDetails {
        return storageManager.getFromPath(path)?.findFile("details.json")?.let {
            try {
                val data = storageManager.getInputStream(it)!!.use { s ->
                    json.decodeFromStream<AnimeDetails>(s)
                }

                EntryDetails(
                    title = data.title.orEmpty(),
                    titles = listOfNotNull(data.title),
                    authors = data.author.orEmpty(),
                    artists = data.artist.orEmpty(),
                    description = data.description.orEmpty(),
                    genre = data.genre.orEmpty().joinToString(),
                    status = Status.entries.firstOrNull { s ->
                        s.id == data.status
                    } ?: Status.Unknown,
                )
            } catch (e: Exception) {
                Log.i("aniyomi-local-manager", e.stackTraceToString())
                EntryDetails.Empty
            }
        } ?: EntryDetails.Empty
    }

    private val _toastEvent = MutableSharedFlow<UiEvent>()
    val toastEvent = _toastEvent.asSharedFlow()

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
                getDetailsFromDetails(path)
            } else {
                val id = successState.searchIds[selected]!!
                withMinimumDuration(2.seconds) {
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

    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    fun generateJsonString(): String? {
        val details = (state.value as? State.Success)?.details ?: return null

        return prettyJson.encodeToString(
            buildJsonObject {
                put("title", details.title)
                put("author", details.authors)
                put("artist", details.artists)
                put("description", details.description)
                putJsonArray("genre") {
                    addAll(details.genre.split(", "))
                }
                put("status", details.status.id)
            },
        )
    }

    private fun performDownload(): Boolean {
        val data = generateJsonString() ?: return false
        val dir = storageManager.getFromPath(path) ?: return false
        val details = dir.findFile("details.json")
            ?: dir.createFile("application/json", "details.json")
            ?: return false

        storageManager.getOutputStream(details, "wt").use { output ->
            output!!.write(data.toByteArray())
        }

        return true
    }

    fun copyDetails() {
        viewModelScope.launch {
            val data = generateJsonString()
            if (data != null) {
                _toastEvent.emit(UiEvent.Copy(data))
            }
        }
    }

    fun generateDetails() {
        viewModelScope.launch {
            val result = performDownload()
            _toastEvent.emit(UiEvent.Downloaded(result))
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
