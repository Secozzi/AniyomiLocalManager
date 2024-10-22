package xyz.secozzi.aniyomilocalmanager.ui.entry.manga

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.k1rakishou.fsaf.FileManager

class MangaEntryScreenModel(
    private val path: String,
    private val fileManager: FileManager,
) : ScreenModel {
    private val directory = fileManager.fromUri(Uri.parse(path))!!

    fun hasCover(): Boolean {
        return fileManager.listFiles(directory).map {
            fileManager.getName(it)
        }.any { it.contains("cover") }
    }

    fun hasComicInfo(): Boolean {
        return fileManager.findFile(directory, "ComicInfo.xml") != null
    }
}
