package xyz.secozzi.aniyomilocalmanager.ui.manga.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.presentation.manga.entry.MangaEntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data class MangaEntryRoute(val path: String) : NavKey

@Composable
fun MangaEntryScreen(path: String) {
    val backStack = LocalBackStack.current

    val viewModel = koinViewModel<MangaEntryScreenViewModel> {
        parametersOf(path)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state is MangaEntryScreenViewModel.State.Idle) {
        return
    }

    MangaEntryScreenContent(
        state = state as MangaEntryScreenViewModel.State.Success,
        onBack = { backStack.removeLastOrNull() },
        onEditCover = { },
        onEditComicInfo = { },
        onClickMangaBaka = { },
        onClickAnilist = { },
        onClickMal = { },
    )
}
