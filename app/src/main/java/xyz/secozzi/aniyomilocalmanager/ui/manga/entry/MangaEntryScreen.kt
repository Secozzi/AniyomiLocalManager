package xyz.secozzi.aniyomilocalmanager.ui.manga.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.presentation.manga.entry.MangaEntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.manga.cover.MangaCoverRoute
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchResult
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class MangaEntryRoute(val path: String) : NavKey

@Composable
fun MangaEntryScreen(path: String) {
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val viewModel = koinViewModel<MangaEntryScreenViewModel> {
        parametersOf(path)
    }

    val result: String? = resultStore.getResultState<SearchResult>()
    LaunchedEffect(result) {
        if (result == null) return@LaunchedEffect
        resultStore.removeResult<SearchResult>()
        viewModel.updateIds(result)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    MangaEntryScreenContent(
        state = state,
        onBack = { backStack.removeLastOrNull() },
        onEditCover = { backStack.add(MangaCoverRoute(path)) },
        onEditComicInfo = { },
        onClickMangaBaka = {
            val name = (state as MangaEntryScreenViewModel.State.Success).name
            backStack.add(SearchRoute(name, TrackerIds.MangaBaka))
        },
        onClickAnilist = { },
        onClickMal = { },
    )
}
