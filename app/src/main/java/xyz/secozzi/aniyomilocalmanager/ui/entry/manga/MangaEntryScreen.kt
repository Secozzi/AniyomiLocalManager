package xyz.secozzi.aniyomilocalmanager.ui.entry.manga

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.ui.entry.EntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.entry.SelectItem
import xyz.secozzi.aniyomilocalmanager.ui.entry.manga.comicinfo.ComicInfoScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.manga.cover.MangaCoverScreen

class MangaEntryScreen(val path: String) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val fileManager = koinInject<FileManager>()
        val screenModel = rememberScreenModel { MangaEntryScreenModel(path, fileManager) }

        EntryScreenContent(
            path = path,
            onBack = { navigator.pop() }
        ) {
            SelectItem(
                title = stringResource(R.string.entry_create_cover),
                present = screenModel.hasCover(),
                onClick = { navigator.push(MangaCoverScreen(path)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_comicinfo),
                present = screenModel.hasComicInfo(),
                onClick = { navigator.push(ComicInfoScreen(path)) },
            )
        }
    }
}
