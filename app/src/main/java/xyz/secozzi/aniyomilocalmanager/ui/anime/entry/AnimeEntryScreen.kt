package xyz.secozzi.aniyomilocalmanager.ui.anime.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.presentation.anime.entry.AnimeEntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.details.AnimeDetailsRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit.AnimeEditEpisodesRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch.AnimeFetchEpisodesRoute
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchResult
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class AnimeEntryRoute(val path: String) : NavKey

@Composable
fun AnimeEntryScreen(path: String) {
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val viewModel = koinViewModel<AnimeEntryScreenViewModel> {
        parametersOf(path)
    }

    val result: SearchResult? = resultStore.getResultState<SearchResult>()
    LaunchedEffect(result) {
        if (result == null) return@LaunchedEffect
        resultStore.removeResult<SearchResult>()
        viewModel.updateIds(result)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val detailsState by viewModel.detailsState.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()

    AnimeEntryScreenContent(
        state = state,
        detailsState = detailsState,
        name = name,
        onBack = { backStack.removeLastOrNull() },
        onEditCover = { backStack.add(AnimeCoverRoute(path)) },
        onEditDetails = { backStack.add(AnimeDetailsRoute(path)) },
        onFetchEpisodes = { backStack.add(AnimeFetchEpisodesRoute(path)) },
        onEditEpisodes = { backStack.add(AnimeEditEpisodesRoute(path)) },
        onClickAnilist = { backStack.add(SearchRoute(name, SearchIds.AnilistAnime)) },
        onClickAnidb = { backStack.add(SearchRoute(name, SearchIds.AniDB)) },
        onClickMal = { backStack.add(SearchRoute(name, SearchIds.MalAnime)) },
    )
}
