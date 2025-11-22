package xyz.secozzi.aniyomilocalmanager.ui.home.anime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.anggrayudi.storage.file.baseName
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.fullName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.aniyomilocalmanager.domain.home.AnimeListEntry
import xyz.secozzi.aniyomilocalmanager.domain.storage.DETAILS_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODES_JSON
import xyz.secozzi.aniyomilocalmanager.domain.storage.EPISODE_FILE_TYPES
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.DataPreferences
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class AnimeScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val dataPreferences: DataPreferences,
    private val storageManager: StorageManager,
    private val filesComparator: FilesComparator,
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var isLoaded by savedStateHandle.saveable {
        mutableStateOf(false)
    }

    val storageLocationFlow = dataPreferences.animeStorageLocation.stateIn(viewModelScope)
    private val relativePaths = MutableStateFlow<List<String>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3.seconds)
            isLoaded = true
        }

        viewModelScope.launch {
            storageLocationFlow.collectLatest {
                relativePaths.update { _ -> listOf(storageManager.getFile(it)!!.baseName) }
            }
        }
    }

    fun addPathSegment(name: String) {
        relativePaths.update { paths -> paths + name }
    }

    fun onNavigateTo(index: Int) {
        if (index == relativePaths.value.lastIndex) {
            return
        }

        relativePaths.update { paths ->
            paths.subList(0, index + 1)
        }
    }

    fun setStorageLocation(location: String) {
        dataPreferences.animeStorageLocation.set(location)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = combine(storageLocationFlow, relativePaths) { a, b -> a to b }
        .mapLatest { (location, relative) ->
            if (location.isEmpty()) {
                isLoaded = true
                return@mapLatest State.Unset
            }

            _isLoading.update { _ -> true }

            withContext(Dispatchers.IO) {
                val file = storageManager.getFile(location)!!
                val selected = storageManager.getChild(file, relative.drop(1))!!

                val entries = selected.children
                    .filter { it.isDirectory }
                    .sortedWith(filesComparator)
                    .map { dir ->
                        val children = dir.children.filterNot { it.fullName.startsWith(".") }
                        val isSeason = when {
                            children.any { it.extension in EPISODE_FILE_TYPES } -> false
                            children.any { it.isDirectory } -> true
                            else -> false
                        }

                        val names = children.map { it.fullName }

                        AnimeListEntry(
                            isSeason = isSeason,
                            path = storageManager.getPath(dir),
                            name = dir.fullName,
                            lastModified = Instant.ofEpochMilli(dir.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")),
                            size = children.size,
                            hasCover = names.any { it.startsWith("cover", true) },
                            hasDetails = names.any { it == DETAILS_JSON },
                            hasEpisodes = names.any { it == EPISODES_JSON },
                        )
                    }

                isLoaded = true
                _isLoading.update { _ -> false }
                State.Success(
                    relative = relative.toPersistentList(),
                    entries = entries,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = State.Idle,
        )

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Unset : State

        @Immutable
        data class Success(
            val entries: List<AnimeListEntry>,
            val relative: ImmutableList<String>,
        ) : State
    }
}

const val ANIME_DIRECTORY_NAME = "localanime"
