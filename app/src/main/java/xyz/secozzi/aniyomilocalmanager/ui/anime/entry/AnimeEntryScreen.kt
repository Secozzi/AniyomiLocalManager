package xyz.secozzi.aniyomilocalmanager.ui.anime.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.anggrayudi.storage.file.fullName
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.presentation.anime.entry.AnimeEntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverRoute
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class AnimeEntryRoute(val path: String) : NavKey

@Composable
fun AnimeEntryScreen(path: String) {
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val storageManager = koinInject<StorageManager>()
    val name = remember { storageManager.getFromPath(path)!!.fullName }

    val viewModel = koinViewModel<AnimeEntryScreenViewModel> {
        parametersOf(path)
    }

    val result: SearchResultItem? = resultStore.getResultState<SearchResultItem>()
    LaunchedEffect(result) {
        if (result == null) return@LaunchedEffect
        resultStore.removeResult<SearchResultItem>()
        viewModel.updateIds(result)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    AnimeEntryScreenContent(
        state = state,
        name = name,
        onBack = { backStack.removeLastOrNull() },
        onEditCover = { backStack.add(AnimeCoverRoute(path)) },
        onEditDetails = { },
        onEditEpisodes = { },
        onClickAnilist = { backStack.add(SearchRoute(name, SearchIds.AnilistAnime)) },
        onClickAnidb = { },
        onClickMal = { },
    )
}
