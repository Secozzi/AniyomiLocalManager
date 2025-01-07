package xyz.secozzi.aniyomilocalmanager.ui.entry.anime

import android.net.Uri
import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.ui.home.tabs.ANIME_DIRECTORY_NAME
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class AnimeEntryScreenModel(
    private val path: String,
    private val fileManager: FileManager,
    private val trackerIdRepository: TrackerIdRepository,
) : ScreenModel {
    private val _anilistId = MutableStateFlow<Long?>(null)
    val anilistId = _anilistId.asStateFlow()

    private val _anidbId = MutableStateFlow<Long?>(null)
    val anidbId = _anidbId.asStateFlow()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.getTrackerId("$ANIME_DIRECTORY_NAME/${path.getDirectoryName()}").collect { entity ->
                entity?.anilistId?.let { _anilistId.update { _ -> it } }
                entity?.aniDBId?.let { _anidbId.update { _ -> it } }
            }
        }
    }

    private val directory = fileManager.fromUri(Uri.parse(path))!!

    fun hasCover(): Boolean {
        return fileManager.listFiles(directory).map {
            fileManager.getName(it)
        }.any { it.contains("cover") }
    }

    fun hasDetailsJson(): Boolean {
        return fileManager.findFile(directory, "details.json") != null
    }

    fun hasEpisodesJson(): Boolean {
        return fileManager.findFile(directory, "episodes.json") != null
    }

    fun updateAniList(anilistId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.updateAniListId(
                path = "$ANIME_DIRECTORY_NAME/${path.getDirectoryName()}",
                anilistId = anilistId,
            )
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
}
