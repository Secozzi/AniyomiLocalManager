package xyz.secozzi.aniyomilocalmanager.ui.entry.manga

import android.net.Uri
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
import xyz.secozzi.aniyomilocalmanager.ui.home.tabs.MANGA_DIRECTORY_NAME
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class MangaEntryScreenModel(
    private val path: String,
    private val fileManager: FileManager,
    private val trackerIdRepository: TrackerIdRepository,
) : ScreenModel {
    private val _anilistId = MutableStateFlow<Long?>(null)
    val anilistId = _anilistId.asStateFlow()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.getTrackerId("$MANGA_DIRECTORY_NAME/${path.getDirectoryName()}").collect { entity ->
                entity?.anilistId?.let { _anilistId.update { _ -> it } }
            }
        }
    }

    private val directory = fileManager.fromUri(Uri.parse(path))!!

    fun hasCover(): Boolean {
        return fileManager.listFiles(directory).map {
            fileManager.getName(it)
        }.any { it.contains("cover") }
    }

    fun hasComicInfo(): Boolean {
        return fileManager.findFile(directory, "ComicInfo.xml") != null
    }

    fun updateDatabase(anilistId: Long? = null) {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.upsert(
                TrackerIdEntity(
                    path = "$MANGA_DIRECTORY_NAME/${path.getDirectoryName()}",
                    anilistId = anilistId,
                )
            )
        }
    }
}
