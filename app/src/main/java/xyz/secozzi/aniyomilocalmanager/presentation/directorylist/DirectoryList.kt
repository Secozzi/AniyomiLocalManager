package xyz.secozzi.aniyomilocalmanager.presentation.directorylist

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator

@Composable
fun DirectoryList(
    storagePath: String,
    isAnime: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onError: @Composable () -> Unit,
) {
    val fileManager = koinInject<FileManager>()

    val directory = fileManager.fromUri(Uri.parse(storagePath))
    if (directory == null) {
        onError()
    } else {
        val directoryList = fileManager.listFiles(directory).filter { file ->
            fileManager.isDirectory(file)
        }.sortedWith(FilesComparator(fileManager))

        LazyColumn(modifier) {
            itemsIndexed(directoryList, key = { _, dir -> fileManager.getName(dir) }) { index, dir ->
                val data = if (isAnime) getLocalAnimeData(fileManager, dir) else getLocalMangaData(fileManager, dir)
                DirectoryListing(
                    data = data,
                    modifier = Modifier.background(
                        if (index % 2 == 1) {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                    ),
                    onClick = { onClick(dir.getFullPath()) },
                )
            }
        }
    }
}
