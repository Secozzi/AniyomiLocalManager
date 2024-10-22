package xyz.secozzi.aniyomilocalmanager.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ProgressContent
import xyz.secozzi.aniyomilocalmanager.presentation.search.components.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.presentation.search.components.SearchTopBar
import xyz.secozzi.aniyomilocalmanager.presentation.util.plus
import xyz.secozzi.aniyomilocalmanager.presentation.util.popWithResult
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

class SearchScreen(
    private val searchQuery: String,
    private val searchRepositoryId: Long,
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val searchRepositoryManager = koinInject<SearchRepositoryManager>()
        val searchRepo = searchRepositoryManager.getRepo(searchRepositoryId)
        val screenModel = rememberScreenModel { SearchSreeenModel(searchRepo) }

        val focusRequester = remember { FocusRequester() }

        val selectedItem by screenModel.selectedItem.collectAsState()
        val searchItems by screenModel.searchItems.collectAsState()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Scaffold(
            topBar = {
                SearchTopBar(
                    onBack = {
                        navigator.pop()
                    },
                    searchQuery = searchQuery,
                    onSearch = screenModel::search,
                    focusRequester = focusRequester,
                )
            },
            bottomBar = {
                if (selectedItem != null) {
                    Button(
                        onClick = {
                            navigator.popWithResult(
                                selectedItem!!
                            )
                        },
                        modifier = Modifier
                            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                                bottom = MaterialTheme.spacing.smaller,
                            )
                            .fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.select_action))
                    }
                }
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            searchItems.DisplayResult(
                onIdle = {},
                onLoading = { ProgressContent(modifier = paddingModifier) },
                onError = { ErrorContent(it, modifier = paddingModifier) },
                onSuccess = { values ->
                    SearchResultsList(
                        values = values,
                        selectedItem = selectedItem,
                        paddingValues = paddingValues,
                        onItemClick = { screenModel.updateSelected(it) }
                    )
                }
            )
        }
    }
}

@Composable
fun SearchResultsList(
    values: List<SearchDataItem>,
    selectedItem: SearchDataItem?,
    paddingValues: PaddingValues,
    onItemClick: (SearchDataItem) -> Unit,
) {
    LazyColumn(
        contentPadding = paddingValues + PaddingValues(vertical = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        items(
            items = values,
            key = { it.hashCode() }
        ) { searchItem ->
            SearchResultItem(
                searchItem = searchItem.toSearchItem(),
                selected = selectedItem == searchItem,
                onClick = { onItemClick(searchItem) }
            )
        }
    }
}
