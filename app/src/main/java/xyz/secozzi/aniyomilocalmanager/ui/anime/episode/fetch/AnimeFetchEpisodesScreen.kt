package xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch

import android.content.ClipData
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.AnimeFetchEpisodesScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class AnimeFetchEpisodesRoute(val path: String) : NavKey

@Composable
fun AnimeFetchEpisodesScreen(path: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val resources = LocalResources.current
    val clipboard = LocalClipboard.current
    val resultStore = LocalResultStore.current

    val viewModel = koinViewModel<AnimeFetchEpisodesScreenViewModel> {
        parametersOf(path)
    }

    val result: SearchResultItem? = resultStore.getResultState<SearchResultItem>()
    LaunchedEffect(result) {
        if (result == null) return@LaunchedEffect
        resultStore.removeResult<SearchResultItem>()
        viewModel.updateIds(result)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedSearch by viewModel.selectedSearch.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val episodes by viewModel.episodes.collectAsStateWithLifecycle()

    val videoCount by viewModel.videoCount.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val start by viewModel.start.collectAsStateWithLifecycle()
    val end by viewModel.end.collectAsStateWithLifecycle()
    val offset by viewModel.offset.collectAsStateWithLifecycle()

    CollectAsEffect(viewModel.uiEvent) {
        when (it) {
            is AnimeFetchEpisodesScreenViewModel.UiEvent.Copy -> {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(it.text, it.text)))
            }
            is AnimeFetchEpisodesScreenViewModel.UiEvent.Downloaded -> {
                val message = if (it.success) {
                    resources.getString(R.string.episode_generate_details_success)
                } else {
                    resources.getString(R.string.episode_generate_details_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AnimeFetchEpisodesScreenContent(
        state = state,
        selectedSearch = selectedSearch,
        name = name,
        isLoading = isLoading,
        episodesMap = episodes,
        onBack = { backStack.removeLastOrNull() },
        onClickSearch = { backStack.add(SearchRoute(name, SearchIds.MalAnime)) },
        onClickSearchId = viewModel::updateSelectedSearchId,
        onGenerate = viewModel::generateDetails,
        onCopy = viewModel::copyDetails,

        videoCount = videoCount,
        selectedType = selectedType,
        onSelectedTypeChange = viewModel::updateSelectedType,
        start = start,
        onStartChange = viewModel::updateStart,
        end = end,
        onEndChange = viewModel::updateEnd,
        offset = offset,
        onOffsetChange = viewModel::updateOffset,
    )
}
