package xyz.secozzi.aniyomilocalmanager.ui.manga.chapters

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.baseName
import com.anggrayudi.storage.file.children
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.fullName
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.adaptivity.xmlutil.newReader
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.domain.entry.manga.model.ComicInfo
import xyz.secozzi.aniyomilocalmanager.domain.storage.ARCHIVE_FILE_TYPES
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveReader
import xyz.secozzi.aniyomilocalmanager.domain.storage.COMIC_INFO_FILE
import xyz.secozzi.aniyomilocalmanager.domain.storage.IMAGE_FILE_TYPES
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.domain.storage.ZipWriter
import xyz.secozzi.aniyomilocalmanager.utils.ChapterRecognition
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel
import xyz.secozzi.aniyomilocalmanager.utils.copyAt

class MangaChaptersScreenViewModel(
    private val path: String,
    private val storageManager: StorageManager,
    private val xml: XML,
) : StateViewModel<MangaChaptersScreenViewModel.State>(State.Idle), KoinComponent {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _unsaved = MutableStateFlow<ImmutableSet<Int>>(persistentSetOf())
    val unsaved = _unsaved.asStateFlow()

    private val _loading = MutableStateFlow<ImmutableSet<Int>>(persistentSetOf())
    val loading = _loading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = storageManager.getFromPath(path)!!

            _name.update { _ -> dir.fullName }
            val state = try {
                val chapters = dir.children.mapNotNull { c ->
                    if ((c.isFile && c.extension !in ARCHIVE_FILE_TYPES) || c.fullName.startsWith(".")) {
                        return@mapNotNull null
                    }
                    val parsed = parseComicInfo(c)

                    Entry(
                        data = parsed ?: generateComicInfo(c.baseName),
                        path = storageManager.getPath(c),
                        isDirectory = c.isDirectory,
                        isNewComicInfo = parsed == null,
                    )
                }
                    .sortedBy { it.data.number?.value?.toFloatOrNull() ?: 1f }

                _unsaved.update { _ ->
                    chapters.withIndex()
                        .filter { it.value.isNewComicInfo }
                        .map { it.index }
                        .toPersistentSet()
                }

                State.Success(
                    data = chapters,
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                State.Error(e)
            }

            mutableState.update { _ -> state }
        }
    }

    private fun generateComicInfo(name: String): ComicInfo {
        val chapterNumber = ChapterRecognition.parseChapterNumber(name)
        return ComicInfo(
            title = ComicInfo.Title(name),
            number = ComicInfo.Number(chapterNumber.toString()),
            translator = null,
            series = null,
            summary = null,
            writer = null,
            penciller = null,
            genre = null,
            publishingStatus = null,
        )
    }

    private fun parseComicInfo(file: DocumentFile): ComicInfo? {
        return if (file.isDirectory) {
            val comicInfoFile = file.findFile(COMIC_INFO_FILE)
                ?: return null

            storageManager.getInputStream(comicInfoFile)?.use { r ->
                val xmlReader = xmlStreaming.newReader(r)
                xml.decodeFromReader<ComicInfo>(xmlReader)
            } ?: return null
        } else {
            if (file.extension in ARCHIVE_FILE_TYPES) {
                val inputStream = storageManager.getFileDescriptor(file, "r")?.use { t ->
                    val reader: ArchiveReader = get { parametersOf(t) }
                    reader.getInputStream(COMIC_INFO_FILE)
                } ?: return null

                inputStream.use { r ->
                    val xmlReader = xmlStreaming.newReader(r)
                    xml.decodeFromReader<ComicInfo>(xmlReader)
                }
            } else {
                null
            }
        }
    }

    fun onEditTitle(index: Int, value: String) {
        updateEntry(index) {
            it.copy(title = ComicInfo.Title(value))
        }
    }

    fun onEditNumber(index: Int, value: String) {
        updateEntry(index) {
            it.copy(number = ComicInfo.Number(value.ifBlank { "-1" }))
        }
    }

    fun onEditScanlator(index: Int, value: String) {
        updateEntry(index) {
            it.copy(translator = ComicInfo.Translator(value))
        }
    }

    private fun updateEntry(index: Int, func: (ComicInfo) -> ComicInfo) {
        val successState = state.value as? State.Success ?: return
        val chapters = successState.data.copyAt(index) { c ->
            c.copy(data = func(c.data))
        }
        _unsaved.update { u -> (u + index).toPersistentSet() }
        mutableState.update { _ -> State.Success(chapters) }
    }

    fun onSave(index: Int) {
        val successState = state.value as? State.Success ?: return
        if (index in loading.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _loading.update { l ->
                (l + index).toPersistentSet()
            }

            val entry = successState.data[index]
            val result = saveComicInfo(entry)
            _uiEvent.emit(UiEvent.Saved(result))

            _loading.update { l ->
                (l - index).toPersistentSet()
            }
            if (result) {
                _unsaved.update { u ->
                    (u - index).toPersistentSet()
                }
            }
        }
    }

    private fun saveComicInfo(entry: Entry): Boolean {
        val data = xml.encodeToString(ComicInfo.serializer(), entry.data)
        if (entry.isDirectory) {
            val dir = storageManager.getFromPath(entry.path) ?: return false
            val comicInfo = dir.findFile("ComicInfo.xml")
                ?: dir.createFile("application/xml", "ComicInfo.xml")
                ?: return false

            storageManager.getOutputStream(comicInfo, "wt").use { output ->
                output!!.write(data.toByteArray())
            }
        } else {
            val archive = storageManager.getFromPath(entry.path) ?: return false
            val name = archive.baseName
            archive.renameTo("old.tmp")
            val newArchive = storageManager.getFromPath(path)
                ?.createFile("application/vnd.comicbook+zip", "$name.cbz.tmp")
                ?: return false

            val reader = storageManager.getFileDescriptor(archive, "r")?.use { t ->
                get<ArchiveReader> { parametersOf(t) }
            } ?: return false

            val writer = get<ZipWriter> { parametersOf(newArchive) }
            writer.use { w ->
                reader.useEntries { pages ->
                    pages.forEach { p ->
                        val ext = p.name.split('.').last()
                        if (ext in IMAGE_FILE_TYPES) {
                            reader.getInputStream(p.name)?.use { i ->
                                w.write(p, i)
                            }
                        }
                    }
                }

                data.toByteArray(Charsets.UTF_8).inputStream().use { i ->
                    w.write(COMIC_INFO_FILE, i)
                }
            }

            newArchive.renameTo("$name.cbz")
            archive.delete()
        }

        return true
    }

    @Stable
    data class Entry(
        val data: ComicInfo,
        val path: String,
        val isDirectory: Boolean,
        val isNewComicInfo: Boolean,
    )

    @Immutable
    sealed interface UiEvent {
        @Immutable
        data class Saved(val success: Boolean) : UiEvent
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data class Error(val throwable: Throwable) : State

        @Immutable
        data class Success(
            val data: List<Entry>,
        ) : State
    }
}
