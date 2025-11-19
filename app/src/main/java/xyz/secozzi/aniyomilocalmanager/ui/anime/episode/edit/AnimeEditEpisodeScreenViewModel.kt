package xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.fullName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel
import xyz.secozzi.aniyomilocalmanager.utils.copyAt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalSerializationApi::class)
class AnimeEditEpisodeScreenViewModel(
    private val path: String,
    private val storageManager: StorageManager,
    private val json: Json,
) : StateViewModel<AnimeEditEpisodeScreenViewModel.State>(State.Idle) {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _validIndexes = MutableStateFlow<ImmutableList<ValidEntry>>(persistentListOf())
    val validIndexes = _validIndexes.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _dialog = MutableStateFlow<Dialog?>(null)
    val dialog = _dialog.asStateFlow()

    init {
        viewModelScope.launch {
            flow { emit(path) }.collectLatest { path ->
                _name.update { _ -> storageManager.getFromPath(path)!!.fullName }

                val state = try {
                    val dir = storageManager.getFromPath(path)!!
                    val episodesFile = dir.findFile("episodes.json")
                    val episodes = episodesFile?.let { ep ->
                        storageManager.getInputStream(ep)?.use {
                            json.decodeFromStream<List<EpisodeDetails>>(it)
                        }
                    } ?: emptyList()

                    _validIndexes.update {
                        List(episodes.size) {
                            ValidEntry(isDuplicate = false, isValidDate = true)
                        }.toPersistentList()
                    }

                    State.Success(
                        data = episodes,
                    )
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    State.Error(e)
                }
                mutableState.update { _ -> state }
            }
        }
    }

    fun onEditTitle(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(name = value) }
        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditNumber(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(episodeNumber = value.toFloat()) }

        val duplicates = episodes.map { it.episodeNumber }
            .withIndex()
            .groupBy { it.value }
            .filter { it.value.size > 1 }
            .flatMap { it.value.map { t -> t.index } }

        _validIndexes.update { v ->
            v.mapIndexed { i, entry ->
                entry.copy(isDuplicate = i in duplicates)
            }.toPersistentList()
        }

        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditDescription(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(summary = value) }
        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditFiller(index: Int, value: Boolean) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(fillermark = value) }
        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditPreviewUrl(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(previewUrl = value) }
        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditScanlator(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(scanlator = value) }
        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun onEditDate(index: Int, value: String) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.copyAt(index) { it.copy(dateUpload = value) }

        try {
            if (value != NO_DATE) {
                LocalDateTime.parse(value, DATE_FORMAT)
            }
            _validIndexes.update { v ->
                v.copyAt(index) { it.copy(isValidDate = true) }.toPersistentList()
            }
        } catch (_: DateTimeParseException) {
            _validIndexes.update { v ->
                v.copyAt(index) { it.copy(isValidDate = false) }.toPersistentList()
            }
        }

        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    private fun performSave(): Boolean {
        val successState = state.value as? State.Success ?: return false
        val episodes = successState.data.map {
            it.copy(
                dateUpload = it.dateUpload.takeUnless { d -> d == NO_DATE },
                name = it.name.takeIf { v -> v?.isNotBlank() == true },
                scanlator = it.scanlator.takeIf { v -> v?.isNotBlank() == true },
                summary = it.summary.takeIf { v -> v?.isNotBlank() == true },
                previewUrl = it.previewUrl.takeIf { v -> v?.isNotBlank() == true },
            )
        }

        val data = prettyJson.encodeToString(episodes)

        val dir = storageManager.getFromPath(path) ?: return false
        val details = dir.findFile("episodes.json")
            ?: dir.createFile("application/json", "episodes.json")
            ?: return false

        storageManager.getOutputStream(details, "wt").use { output ->
            output!!.write(data.toByteArray())
        }

        return true
    }

    fun save() {
        viewModelScope.launch {
            val result = performSave()
            _uiEvent.emit(UiEvent.Downloaded(result))
        }
    }

    fun onAdd() {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data
        val newEpisodeNumber = episodes.maxOf { it.episodeNumber } + 1
        val newEpisode = EpisodeDetails(
            episodeNumber = newEpisodeNumber,
            name = "",
            dateUpload = NO_DATE,
            fillermark = false,
            scanlator = "",
            summary = "",
            previewUrl = "",
        )

        mutableState.update { _ ->
            State.Success(episodes + newEpisode)
        }

        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ScrollTo(episodes.lastIndex + 1))
        }
    }

    fun onDelete(index: Int) {
        val successState = state.value as? State.Success ?: return
        val episodes = successState.data.filterIndexed { i, _ -> i != index }

        mutableState.update { _ ->
            State.Success(episodes)
        }
    }

    fun showDeleteDialog(index: Int) {
        _dialog.update { _ -> Dialog.ConfirmDelete(index) }
    }

    fun dismissDialog() {
        _dialog.update { _ -> null }
    }

    @Stable
    data class ValidEntry(
        val isDuplicate: Boolean,
        val isValidDate: Boolean,
    ) {
        fun isValid(): Boolean = !isDuplicate && isValidDate
    }

    @Immutable
    sealed interface Dialog {
        @Immutable
        data class ConfirmDelete(val index: Int) : Dialog
    }

    @Immutable
    sealed interface UiEvent {
        @Immutable
        data class ScrollTo(val index: Int) : UiEvent

        @Immutable
        data class Downloaded(val success: Boolean) : UiEvent
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val throwable: Throwable) : State

        @Immutable
        data class Success(
            val data: List<EpisodeDetails>,
        ) : State
    }

    companion object {
        private const val NO_DATE = "0000-00-00T00:00:00"
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    }
}
