package xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.fullName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODES_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODE_FILE_TYPES
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchResult
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel
import xyz.secozzi.aniyomilocalmanager.utils.asResultFlow
import xyz.secozzi.aniyomilocalmanager.utils.withMinimumDuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AnimeFetchEpisodesScreenViewModel(
    private val path: String,
    private val trackerRepository: AnimeTrackerRepository,
    private val storageManager: StorageManager,
    private val searchManager: SearchManager,
) : StateViewModel<AnimeFetchEpisodesScreenViewModel.State>(State.Idle) {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    val videoCount = flow { emit(path) }
        .asResultFlow(
            idleResult = null,
            loadingResult = null,
            getErrorResult = { 0 },
            dispatcher = Dispatchers.IO,
        ) {
            val dir = storageManager.getFromPath(it)
            dir?.children?.count { e ->
                e.extension in EPISODE_FILE_TYPES
            } ?: 0
        }

    @OptIn(ExperimentalSerializationApi::class)
    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    init {
        viewModelScope.launch {
            _name.update { _ ->
                storageManager.getFromPath(path)!!.fullName
            }
        }

        viewModelScope.launch {
            trackerRepository.getTrackData(path)
                .distinctUntilChanged()
                .collectLatest { data ->
                    val entity = data ?: run {
                        mutableState.update { _ -> State.NoID }
                        return@collectLatest
                    }

                    if (entity.anidb != null) {
                        _selectedSearch.update { _ -> SearchIds.AniDB }
                    } else if (entity.mal != null) {
                        _selectedSearch.update { _ -> SearchIds.MalAnime }
                    } else {
                        mutableState.update { _ -> State.NoID }
                        return@collectLatest
                    }

                    mutableState.update { _ ->
                        State.Success(
                            searchIds = buildMap(2) {
                                if (entity.anidb != null) {
                                    put(SearchIds.AniDB, entity.anidb.toString())
                                }
                                if (entity.mal != null) {
                                    put(SearchIds.MalAnime, entity.mal.toString())
                                }
                            }.toPersistentMap(),
                        )
                    }

                    if (entity.anidb != null) {
                        updateSelectedSearchId(SearchIds.AniDB)
                    } else {
                        updateSelectedSearchId(SearchIds.MalAnime)
                    }
                }
        }
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

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedSearch = MutableStateFlow<SearchIds?>(null)
    val selectedSearch = _selectedSearch.asStateFlow()

    private val _episodes =
        MutableStateFlow<ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>>(persistentMapOf())
    val episodes = _episodes.asStateFlow()

    private val _selectedType = MutableStateFlow<EpisodeType?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _start = MutableStateFlow(1)
    val start = _start.asStateFlow()

    private val _end = MutableStateFlow(1)
    val end = _end.asStateFlow()

    private val _offset = MutableStateFlow(0)
    val offset = _offset.asStateFlow()

    fun updateStart(value: Int) {
        _start.update { _ -> value }
    }

    fun updateEnd(value: Int) {
        _end.update { _ -> value }
    }

    fun updateOffset(value: Int) {
        _offset.update { _ -> value }
    }

    fun updateSelectedSearchId(selected: SearchIds) {
        _selectedSearch.update { _ -> selected }

        val successState = state.value as? State.Success ?: return
        val id = successState.searchIds[selected] ?: return

        _isLoading.update { _ -> true }

        viewModelScope.launch(Dispatchers.IO) {
            val episodes = try {
                withMinimumDuration(1.5.seconds, 150.milliseconds) {
                    searchManager.getEpisodeRepository(selected).getEpisodesFromId(id)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _isLoading.update { _ -> false }
                mutableState.update { _ -> State.Error(e) }
                return@launch
            }

            episodes.entries.firstOrNull()?.let { (type, ep) ->
                _start.update { _ -> 1 }
                _end.update { _ -> ep.size }
                _offset.update { _ -> 0 }
                _selectedType.update { _ -> type }
            }

            _episodes.update { _ -> episodes }
            _isLoading.update { _ -> false }
        }
    }

    fun updateSelectedType(type: EpisodeType) {
        _selectedType.update { _ -> type }
        episodes.value.entries.firstOrNull { it.key == type }?.let { (_, ep) ->
            _start.update { _ -> 1 }
            _end.update { _ -> ep.size }
            _offset.update { _ -> 0 }
        }
    }

    fun generateJsonString(): String? {
        val episodeList = episodes.value.entries.firstOrNull { (type, _) ->
            type == selectedType.value
        }
            ?.value
            ?.subList(start.value - 1, end.value)
            ?.map {
                it.copy(episodeNumber = it.episodeNumber + offset.value)
            }
            ?: return null

        return prettyJson.encodeToString(episodeList)
    }

    private fun performDownload(): Boolean {
        val data = generateJsonString() ?: return false
        val dir = storageManager.getFromPath(path) ?: return false
        val details = dir.findFile(EPISODES_JSON)
            ?: dir.createFile("application/json", EPISODES_JSON)
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
                _uiEvent.emit(UiEvent.Copy(data))
            }
        }
    }

    fun generateDetails() {
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
        data object NoID : State

        @Immutable
        data class Success(
            val searchIds: ImmutableMap<SearchIds, String>,
        ) : State
    }
}
