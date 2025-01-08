package xyz.secozzi.aniyomilocalmanager.ui.entry

import android.net.Uri
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import com.github.k1rakishou.fsaf.file.FileDescriptorMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import nl.adaptivity.xmlutil.serialization.XML
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearch
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALAnime
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALManga
import xyz.secozzi.aniyomilocalmanager.domain.model.ComicInfo
import xyz.secozzi.aniyomilocalmanager.domain.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName
import java.io.FileWriter

class DetailsScreenModel(
    private val path: String,
    private val anilistId: Long?,
    private val anilistSearchType: AnilistSearchType,
    private val fileManager: FileManager,
    private val xml: XML,
    private val trackerIdRepository: TrackerIdRepository,
    private val anilistSearch: AnilistSearch,
    private val aniListPreferences: AniListPreferences,
) : StateScreenModel<DetailsScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data object Finished : State()
    }

    init {
        if (anilistId != null) {
            screenModelScope.launch(Dispatchers.IO) {
                mutableState.update { _ -> State.Loading }
                val data = anilistSearch.searchFromId(anilistId, anilistSearchType)
                when (anilistSearchType) {
                    AnilistSearchType.ANIME -> updateAnime(
                        data.toALAnime(
                            titlePreference = aniListPreferences.titleLang.get(),
                            studioCountPreference = aniListPreferences.studioCount.get(),
                        ),
                    )
                    AnilistSearchType.MANGA -> updateManga(
                        data.toALManga(
                            titlePreference = aniListPreferences.titleLang.get(),
                        ),
                    )
                }
                mutableState.update { _ -> State.Finished }
            }
        }
    }

    fun updateAniList(directoryName: String, anilistId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.updateAniListId(
                path = "$directoryName/${path.getDirectoryName()}",
                anilistId = anilistId,
            )
        }
    }

    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    var titles = MutableStateFlow<List<String>>(emptyList())
    var title = MutableStateFlow("")
    var author = MutableStateFlow("")
    var artist = MutableStateFlow("")
    var description = MutableStateFlow("")
    var genre = MutableStateFlow("")
    var status = MutableStateFlow<Status?>(null)

    fun updateTitle(input: String) {
        title.update { _ -> input }
    }

    fun updateAuthor(input: String) {
        author.update { _ -> input }
    }

    fun updateArtist(input: String) {
        artist.update { _ -> input }
    }

    fun updateDescription(input: String) {
        description.update { _ -> input }
    }

    fun updateGenre(input: String) {
        genre.update { _ -> input }
    }

    fun updateStatus(input: Status?) {
        input?.let {
            status.update { _ -> it }
        }
    }

    fun updateManga(input: ALManga) {
        titles.update { _ -> input.titles }
        updateTitle(input.titles.first())
        updateAuthor(input.author)
        updateArtist(input.artist)
        updateDescription(input.description ?: "")
        updateGenre(input.genre)
        updateStatus(input.publishingStatus)
    }

    fun updateAnime(input: ALAnime) {
        titles.update { _ -> input.titles }
        updateTitle(input.titles.first())
        updateAuthor(input.studio)
        updateDescription(input.description ?: "")
        updateGenre(input.genre)
        updateStatus(input.publishingStatus)
    }

    fun generateComicInfoXmlString(): String {
        val comicInfo = ComicInfo(
            series = ComicInfo.Series(title.value),
            summary = description.value.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Summary(it)
            },
            writer = author.value.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Writer(it)
            },
            penciller = artist.value.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Penciller(it)
            },
            genre = genre.value.takeIf { it.isNotBlank() }?.let {
                ComicInfo.Genre(it)
            },
            publishingStatus = ComicInfo.PublishingStatusTachiyomi(
                status.value?.displayName ?: Status.Unknown.displayName,
            ),
        )

        return xml.encodeToString(ComicInfo.serializer(), comicInfo)
    }

    fun generateComicInfoXml(): Boolean {
        val xmlStr = generateComicInfoXmlString()

        val dir = fileManager.fromUri(Uri.parse(path)) ?: return false
        val details = fileManager.createFile(dir, "ComicInfo.xml") ?: return false

        fileManager.withFileDescriptor(details, FileDescriptorMode.WriteTruncate) {
            FileWriter(it).use { writer ->
                writer.write(xmlStr)
            }
        }

        return true
    }

    fun generateJsonString(): String {
        return prettyJson.encodeToString(
            buildJsonObject {
                put("title", title.value)
                put("author", author.value)
                put("artist", artist.value)
                put("description", description.value)
                putJsonArray("genre") {
                    addAll(genre.value.split(","))
                }
                put("status", status.value?.id ?: 0)
            },
        )
    }

    fun generateDetailsJson(): Boolean {
        val jsonStr = generateJsonString()

        val dir = fileManager.fromUri(Uri.parse(path)) ?: return false
        val details = fileManager.createFile(dir, "details.json") ?: return false

        fileManager.withFileDescriptor(details, FileDescriptorMode.WriteTruncate) {
            FileWriter(it).use { writer ->
                writer.write(jsonStr)
            }
        }

        return true
    }
}
