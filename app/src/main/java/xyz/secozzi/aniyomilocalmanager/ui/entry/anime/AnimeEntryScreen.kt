package xyz.secozzi.aniyomilocalmanager.ui.entry.anime

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
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.cover.AnimeCoverScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.details.DetailsScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EpisodeScreen

class AnimeEntryScreen(val path: String) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val fileManager = koinInject<FileManager>()
        val screenModel = rememberScreenModel { AnimeEntryScreenModel(path, fileManager) }

        EntryScreenContent(
            path = path,
            onBack = { navigator.pop() }
        ) {
            SelectItem(
                title = stringResource(R.string.entry_create_cover),
                present = screenModel.hasCover(),
                onClick = { navigator.push(AnimeCoverScreen(path)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_details),
                present = screenModel.hasDetailsJson(),
                onClick = { navigator.push(DetailsScreen(path)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_episodes),
                present = screenModel.hasEpisodesJson(),
                onClick = { navigator.push(EpisodeScreen(path)) },
            )
        }
    }
}
