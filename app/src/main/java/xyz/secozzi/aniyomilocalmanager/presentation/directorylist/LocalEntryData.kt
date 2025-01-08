package xyz.secozzi.aniyomilocalmanager.presentation.directorylist

import com.github.k1rakishou.fsaf.FileManager
import com.github.k1rakishou.fsaf.file.AbstractFile

data class LocalEntryData(
    val name: String,
    val lastModified: Long?,
    val size: Int,
    val hasInfo: List<Boolean>,
)

fun getLocalAnimeData(
    fileManager: FileManager,
    dir: AbstractFile,
): LocalEntryData {
    val fileList = fileManager.listFiles(dir).map {
        fileManager.getName(it)
    }

    return LocalEntryData(
        name = fileManager.getName(dir),
        lastModified = fileManager.lastModified(dir),
        size = fileList.size,
        hasInfo = listOf(
            fileList.any { it.contains("cover") },
            fileList.any { it == "details.json" },
            fileList.any { it == "episodes.json" },
        ),
    )
}

fun getLocalMangaData(
    fileManager: FileManager,
    dir: AbstractFile,
): LocalEntryData {
    val fileList = fileManager.listFiles(dir).map {
        fileManager.getName(it)
    }

    return LocalEntryData(
        name = fileManager.getName(dir),
        lastModified = fileManager.lastModified(dir),
        size = fileList.size,
        hasInfo = listOf(
            fileList.any { it.contains("cover") },
            fileList.any { it == "ComicInfo.xml" },
        ),
    )
}
