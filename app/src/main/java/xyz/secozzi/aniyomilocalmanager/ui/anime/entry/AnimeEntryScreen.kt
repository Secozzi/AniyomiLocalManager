package xyz.secozzi.aniyomilocalmanager.ui.anime.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.presentation.anime.entry.AnimeEntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data class AnimeEntryRoute(val path: String) : NavKey

@Composable
fun AnimeEntryScreen(path: String) {
    val backStack = LocalBackStack.current

    val viewModel = koinViewModel<AnimeEntryScreenViewModel> {
        parametersOf(path)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    if (state is AnimeEntryScreenViewModel.State.Idle) {
        return
    }

    AnimeEntryScreenContent(
        state = state as AnimeEntryScreenViewModel.State.Success,
        onBack = { backStack.removeLastOrNull() },
        onEditCover = { },
        onEditDetails = { },
        onEditEpisodes = { },
        onClickAnilist = { },
        onClickAnidb = { },
        onClickMal = { },
    )
}
