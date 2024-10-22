package xyz.secozzi.aniyomilocalmanager.ui.entry.anime

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.k1rakishou.fsaf.FileManager

class AnimeEntryScreenModel(
    private val path: String,
    private val fileManager: FileManager,
) : ScreenModel {
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
}
