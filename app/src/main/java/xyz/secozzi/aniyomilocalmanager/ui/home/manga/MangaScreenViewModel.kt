package xyz.secozzi.aniyomilocalmanager.ui.home.manga

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.fullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import xyz.secozzi.aniyomilocalmanager.domain.home.MangaListEntry
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.DataPreferences
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class MangaScreenViewModel(
    private val dataPreferences: DataPreferences,
    private val storageManager: StorageManager,
    private val filesComparator: FilesComparator,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded = _isLoaded.asStateFlow()

    fun setStorageLocation(location: String) {
        dataPreferences.mangaStorageLocation.set(location)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = dataPreferences.mangaStorageLocation.stateIn(viewModelScope)
        .mapLatest { location ->
            if (location.isEmpty()) {
                _isLoaded.update { _ -> true }
                return@mapLatest State.Unset
            }

            _isLoading.update { _ -> true }

            withContext(Dispatchers.IO) {
                val file = storageManager.getFile(location)!!

                val entries = file.children
                    .filter { it.isDirectory }
                    .sortedWith(filesComparator)
                    .map { dir ->
                        val children = dir.children
                        val names = children.map { it.fullName }

                        MangaListEntry(
                            name = dir.fullName,
                            lastModified = Instant.ofEpochMilli(dir.lastModified())
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")),
                            size = children.size,
                            hasCover = names.any { it.contains("cover") },
                            hasComicInfo = names.any { it.equals("comicinfo.xml", true) },
                        )
                    }

                _isLoaded.update { _ -> true }
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
