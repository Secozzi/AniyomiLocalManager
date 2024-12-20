package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import com.github.k1rakishou.fsaf.file.FileDescriptorMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto.EpisodeModel
import xyz.secozzi.aniyomilocalmanager.data.anidb.search.dto.ADBAnime
import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState
import java.io.FileWriter

@Serializable
data class EpisodeInfo(
    val name: String,
    val scanlator: String?,
    @SerialName("episode_number")
    val episodeNumber: Int,
    @SerialName("date_upload")
    val date: String?,
)

class EpisodeScreenModel(
    private val path: String,
    private val episodeRepo: EpisodeRepository,
    private val fileManager: FileManager,
    private val preferences: AniDBPreferences,
) : ScreenModel {
    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    var anidbId = MutableStateFlow(0L)

    var offset = MutableStateFlow(0)
    var start = MutableStateFlow(1)
    var end = MutableStateFlow(1)
    var isValid = MutableStateFlow(true)

    private val _episodes = MutableStateFlow<RequestState<Map<Int, List<EpisodeModel>>>>(RequestState.Idle)
    val episodes = _episodes.asStateFlow()

    var availableTypes = MutableStateFlow<List<EpisodeType>>(emptyList())
    var selectedType = MutableStateFlow<EpisodeType>(EpisodeType.Regular(extraData = 1))

    var startPreview = MutableStateFlow<EpisodeInfo?>(null)
    var endPreview = MutableStateFlow<EpisodeInfo?>(null)

    fun onSearched(item: ADBAnime) {
        anidbId.update { _ -> item.remoteId }
        getEpisodes(item.remoteId)
    }

    fun onSelectedType(input: EpisodeType?) {
        input?.let {
            selectedType.update { _ -> input }
            offset.update { _ -> 0 }
            start.update { _ -> 1 }
            end.update { _ -> input.extraData!! }

            updateStartPreview()
            updateEndPreview()
        }
    }

    fun updateOffset(input: Int) {
        offset.update { _ -> input }
        updateStartPreview()
        updateEndPreview()
    }

    fun updateStart(input: Int) {
        start.update { _ -> input }
        updateIsValid(end.value >= input && input >= 1 && input <= selectedType.value.extraData!!)
    }

    fun updateEnd(input: Int) {
        end.update { _ -> input }
        updateIsValid(end.value >= input && input >= 1 && input <= selectedType.value.extraData!!)
    }

    private fun updateIsValid(input: Boolean) {
        isValid.update { _ -> input }
        if (input) {
            updateStartPreview()
            updateEndPreview()
        }
    }

    fun updateStartPreview() {
        val startEpisode = episodes.value.getSuccessData()[selectedType.value.id]!![start.value - 1].copy(
            episodeNumber = start.value + offset.value
        )
        startPreview.update { _ ->
            EpisodeInfo(
                name = getFormattedString(preferences.nameFormat.get(), startEpisode),
                scanlator = getFormattedString(preferences.scanlatorFormat.get(), startEpisode),
                episodeNumber = startEpisode.episodeNumber,
                date = startEpisode.airingDate,
            )
        }
    }

    fun updateEndPreview() {
        val endEpisode = episodes.value.getSuccessData()[selectedType.value.id]!![end.value - 1].copy(
            episodeNumber = end.value + offset.value
        )
        endPreview.update { _ ->
            EpisodeInfo(
                name = getFormattedString(preferences.nameFormat.get(), endEpisode),
                scanlator = getFormattedString(preferences.scanlatorFormat.get(), endEpisode),
                episodeNumber = endEpisode.episodeNumber,
                date = endEpisode.airingDate,
            )
        }
    }

    private fun getEpisodes(anidbId: Long) {
        _episodes.update { _ -> RequestState.Loading }
        screenModelScope.launch(Dispatchers.IO) {
            _episodes.update { _ ->
                try {
                    val episodes = episodeRepo.getEpisodes(anidbId)
                        .groupBy { it.episodeType.id }

                    availableTypes.update {
                        episodes
                            .map { (type, items) -> when (type) {
                                    1 -> EpisodeType.Regular(extraData = items.size)
                                    2 -> EpisodeType.Special(extraData = items.size)
                                    3 -> EpisodeType.Credit(extraData = items.size)
                                    4 -> EpisodeType.Trailer(extraData = items.size)
                                    5 -> EpisodeType.Parody(extraData = items.size)
                                    else -> EpisodeType.Other(extraData = items.size)
                                }
                            }
                    }

                    selectedType.update {
                        _ -> availableTypes.value.first()
                    }

                    start.update { _ -> 1 }
                    end.update { _ -> selectedType.value.extraData!! }

                    RequestState.Success(episodes)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }

            if (_episodes.value is RequestState.Success) {
                updateStartPreview()
                updateEndPreview()
            }
        }
    }

    fun generateJsonString(): String {
        return prettyJson.encodeToString(
            episodes.value.getSuccessData()[selectedType.value.id]!!.subList(start.value - 1, end.value).map { ep ->
                val name = getFormattedString(preferences.nameFormat.get(), ep).takeIf { it.isNotBlank() }
                val scanlator = getFormattedString(preferences.scanlatorFormat.get(), ep).takeIf { it.isNotBlank() }

                buildJsonObject {
                    put("episode_number", ep.episodeNumber + offset.value)
                    name?.let {
                        put("name", it)
                    }
                    scanlator?.let {
                        put("scanlator", it)
                    }
                    ep.airingDate?.let {
                        put("date_upload", "${it}T00:00:00")
                    }
                }
            }
        )
    }

    fun generateEpisodesJson(): Boolean {
        val jsonStr = generateJsonString()

        val dir = fileManager.fromUri(Uri.parse(path)) ?: return false
        val details = fileManager.createFile(dir, "episodes.json") ?: return false

        fileManager.withFileDescriptor(details, FileDescriptorMode.WriteTruncate) {
            FileWriter(it).use { writer ->
                writer.write(jsonStr)
            }
        }

        return true
    }

    fun getVideoCount(): Int {
        val directory = fileManager.fromUri(Uri.parse(path))!!
        val directoryList = fileManager.listFiles(directory).filter { file ->
            val ext = fileManager.getName(file).split(".").last()
            fileManager.isFile(file) && ext in SUPPORTED_ARCHIVE_TYPES
        }

        return directoryList.size
    }

    private fun getFormattedString(format: String, data: EpisodeModel): String {
        return format
            .replaceIfNotNull("%eng", data.englishTitle)
            .replaceIfNotNull("%rom", data.romajiTitle)
            .replaceIfNotNull("%nat", data.nativeTitle)
            .replaceIfNotNull("%ep", data.episodeNumber.toString())
            .replaceIfNotNull("%dur", data.duration?.toString())
            .replaceIfNotNull("%air", data.airingDate)
            .replaceIfNotNull("%rat", data.rating)
            .replaceIfNotNull("%sum", data.summary)
            .replaceIfNotNull("%type", data.episodeType.displayName)
    }

    private fun String.replaceIfNotNull(oldValue: String, newValue: String?): String {
        return this.replace(oldValue, newValue ?: "")
    }

    companion object {
        private val SUPPORTED_ARCHIVE_TYPES = listOf("avi", "flv", "mkv", "mov", "mp4", "webm", "wmv")
    }
}
