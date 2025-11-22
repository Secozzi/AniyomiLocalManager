package xyz.secozzi.aniyomilocalmanager.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.domain.search.service.toTrackerId
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class SearchRoute(
    val query: String,
    val searchRepositoryId: SearchIds,
) : NavKey

typealias SearchResult = ImmutableMap<TrackerIds, Long>

@Composable
fun SearchScreen(query: String, searchRepositoryId: SearchIds) {
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val viewModel = koinViewModel<SearchScreenViewModel> {
        parametersOf(query, searchRepositoryId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CollectAsEffect(viewModel.uiEvent) {
        when (it) {
            is SearchScreenViewModel.UiEvent.SearchId -> {
                val map = persistentMapOf(searchRepositoryId.toTrackerId() to it.id)
                resultStore.setResult<SearchResult>(result = map)
                backStack.removeLastOrNull()
            }
        }
    }

    SearchScreenContent(
        query = query,
        state = state,
        selected = selected,
        focusRequester = focusRequester,
        onBack = { backStack.removeLastOrNull() },
        onSearch = viewModel::search,
        onSelect = viewModel::updateSelected,
        onClickSelect = {
            resultStore.setResult<SearchResult>(result = it.trackerIds.toPersistentMap())
            backStack.removeLastOrNull()
        },
    )
}
