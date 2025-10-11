package xyz.secozzi.aniyomilocalmanager.ui.home.anime

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.baseName
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.DataPreferences
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class AnimeScreenViewModel(
    private val dataPreferences: DataPreferences,
    private val storageManager: StorageManager,
    private val filesComparator: FilesComparator,
) : ViewModel() {
    val storageLocationFlow = dataPreferences.animeStorageLocation.stateIn(viewModelScope)
    private val relativePaths = MutableStateFlow<List<String>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    init {
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
                _isLoaded.update { _ -> true }
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
                        val children = dir.children
                        val isSeason = when {
                            children.any { it.extension in EPISODE_FILE_TYPES } -> false
                            children.any { it.isDirectory } -> true
                            else -> false
                        }

                        val names = children.map { it.fullName }

                        AnimeListEntry(
                            isSeason = isSeason,
                            name = dir.fullName,
                            lastModified = Instant.ofEpochMilli(dir.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")),
                            size = children.size,
                            hasCover = names.any { it.contains("cover") },
                            hasDetails = names.any { it == "details.json" },
                            hasEpisodes = names.any { it == "episodes.json" },
                        )
                    }

                _isLoaded.update { _ -> true }
                _isLoading.update { _ -> false }
                State.Success(
                    relative = relative,
                    entries = entries,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = State.Idle,
        )

    companion object {
        private val EPISODE_FILE_TYPES = listOf("avi", "flv", "mkv", "mov", "mp4", "webm", "wmv")
    }

    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Unset : State

        @Immutable
        data class Success(
            val entries: List<AnimeListEntry>,
            val relative: List<String>,
        ) : State
    }
}

const val ANIME_DIRECTORY_NAME = "localanime"
