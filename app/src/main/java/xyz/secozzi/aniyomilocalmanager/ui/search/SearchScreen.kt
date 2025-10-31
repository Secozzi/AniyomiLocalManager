package xyz.secozzi.aniyomilocalmanager.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

typealias SearchResult = String

@Serializable
data class SearchRoute(
    val query: String,
    val searchRepositoryId: Long,
) : NavKey

@Composable
fun SearchScreen(query: String, searchRepositoryId: Long) {
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val json = koinInject<Json>()
    val viewModel = koinViewModel<SearchScreenViewModel> {
        parametersOf(query, searchRepositoryId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
            resultStore.setResult<SearchResult>(result = json.encodeToString(it))
            backStack.removeLastOrNull()
        },
    )
}
