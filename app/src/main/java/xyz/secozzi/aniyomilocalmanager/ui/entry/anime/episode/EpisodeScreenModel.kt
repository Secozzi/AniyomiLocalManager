package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import com.github.k1rakishou.fsaf.file.FileDescriptorMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.asState
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState
import xyz.secozzi.aniyomilocalmanager.ui.home.tabs.ANIME_DIRECTORY_NAME
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName
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
    private val aniDBId: Long?,
    private val episodeRepo: EpisodeRepository,
    private val fileManager: FileManager,
    private val trackerIdRepository: TrackerIdRepository,
    private val preferences: AniDBPreferences,
) : StateScreenModel<RequestState<Map<Int, List<EpisodeModel>>>>(RequestState.Idle) {
    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    private val nameFormat by preferences.nameFormat.asState(screenModelScope)
    private val scanlatorFormat by preferences.scanlatorFormat.asState(screenModelScope)

    init {
        if (aniDBId != null) {
            getEpisodes(aniDBId)
        }
    }

    fun updateAniDB(aniDBId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.updateAniDBId(
                path = "$ANIME_DIRECTORY_NAME/${path.getDirectoryName()}",
                aniDBId = aniDBId,
            )
        }
    }

    var offset = MutableStateFlow(0)
    var start = MutableStateFlow(1)
    var end = MutableStateFlow(1)
    var isValid = MutableStateFlow(true)

    var availableTypes = MutableStateFlow<List<EpisodeType>>(emptyList())
    var selectedType = MutableStateFlow<EpisodeType>(EpisodeType.Regular(extraData = 1))

    var startPreview = MutableStateFlow<EpisodeInfo?>(null)
    var endPreview = MutableStateFlow<EpisodeInfo?>(null)

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

    fun toEpisodeInfo(episodeModel: EpisodeModel): EpisodeInfo {
        return EpisodeInfo(
            name = getFormattedString(nameFormat, episodeModel),
            scanlator = getFormattedString(scanlatorFormat, episodeModel),
            episodeNumber = episodeModel.episodeNumber,
            date = episodeModel.airingDate,
        )
    }

    fun updateStartPreview() {
        val startEpisode = mutableState.value.getSuccessData()[selectedType.value.id]!![start.value - 1].copy(
            episodeNumber = start.value + offset.value
        )
        startPreview.update { _ ->
            EpisodeInfo(
                name = getFormattedString(nameFormat, startEpisode),
                scanlator = getFormattedString(scanlatorFormat, startEpisode),
                episodeNumber = startEpisode.episodeNumber,
                date = startEpisode.airingDate,
            )
        }
    }

    fun updateEndPreview() {
        val endEpisode = mutableState.value.getSuccessData()[selectedType.value.id]!![end.value - 1].copy(
            episodeNumber = end.value + offset.value
        )
        endPreview.update { _ ->
            EpisodeInfo(
                name = getFormattedString(nameFormat, endEpisode),
                scanlator = getFormattedString(scanlatorFormat, endEpisode),
                episodeNumber = endEpisode.episodeNumber,
                date = endEpisode.airingDate,
            )
        }
    }

    fun getEpisodes(anidbId: Long) {
        mutableState.update { _ -> RequestState.Loading }
        screenModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ ->
                try {
                    val episodes = episodeRepo.getEpisodes(anidbId)
                        .groupBy { it.episodeType.id }
                        .toSortedMap()
                        .mapValues { (_, episodes) -> episodes.sortedBy { it.episodeNumber } }

                    availableTypes.update {
                        episodes.map { (type, items) ->
                            when (type) {
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

            if (mutableState.value is RequestState.Success) {
                updateStartPreview()
                updateEndPreview()
            }
        }
    }

    fun generateJsonString(): String {
        return prettyJson.encodeToString(
            mutableState.value.getSuccessData()[selectedType.value.id]!!.subList(start.value - 1, end.value).map { ep ->
                val name = getFormattedString(nameFormat, ep).takeIf { it.isNotBlank() }
                val scanlator = getFormattedString(scanlatorFormat, ep).takeIf { it.isNotBlank() }

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
