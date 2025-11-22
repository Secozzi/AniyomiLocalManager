package xyz.secozzi.aniyomilocalmanager.ui.home.manga

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.secozzi.aniyomilocalmanager.domain.home.MangaListEntry
import xyz.secozzi.aniyomilocalmanager.domain.storage.COMIC_INFO_FILE
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.DataPreferences
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class MangaScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val dataPreferences: DataPreferences,
    private val storageManager: StorageManager,
    private val filesComparator: FilesComparator,
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var isLoaded by savedStateHandle.saveable {
        mutableStateOf(false)
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setStorageLocation(location: String) {
        dataPreferences.mangaStorageLocation.set(location)
    }

    init {
        viewModelScope.launch {
            delay(3.seconds)
            isLoaded = true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = dataPreferences.mangaStorageLocation.stateIn(viewModelScope)
        .mapLatest { location ->
            if (location.isEmpty()) {
                isLoaded = true
                return@mapLatest State.Unset
            }

            _isLoading.update { _ -> true }

            withContext(Dispatchers.IO) {
                val file = storageManager.getFile(location)!!

                val entries = file.children
                    .filter { it.isDirectory }
                    .sortedWith(filesComparator)
                    .map { dir ->
                        val children = dir.children.filterNot { it.fullName.startsWith(".") }
                        val names = children.map { it.fullName }

                        MangaListEntry(
                            path = storageManager.getPath(dir),
                            name = dir.fullName,
                            lastModified = Instant.ofEpochMilli(dir.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")),
                            size = children.size,
                            hasCover = names.any { it.startsWith("cover", true) },
                            hasComicInfo = names.any { it.equals(COMIC_INFO_FILE, true) },
                        )
                    }

                isLoaded = true
                _isLoading.update { _ -> false }
                State.Success(
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
            val entries: List<MangaListEntry>,
        ) : State
    }
}

const val MANGA_DIRECTORY_NAME = "local"
