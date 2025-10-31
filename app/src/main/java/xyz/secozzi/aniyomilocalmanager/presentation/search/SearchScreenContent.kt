package xyz.secozzi.aniyomilocalmanager.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.LoadingContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.SearchTopBar
import xyz.secozzi.aniyomilocalmanager.presentation.utils.plus
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreenContent(
    query: String,
    state: SearchScreenViewModel.State,
    selected: SearchResultItem?,
    focusRequester: FocusRequester,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onSelect: (SearchResultItem) -> Unit,
    onClickSelect: (SearchResultItem) -> Unit,
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                onBack = onBack,
                searchQuery = query,
                onSearch = onSearch,
                focusRequester = focusRequester,
            )
        },
        bottomBar = {
            if (selected != null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = { onClickSelect(selected) },
                        modifier = Modifier
                            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                            .padding(bottom = MaterialTheme.spacing.smaller),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.spacing.larger),
                        )
                        Spacer(Modifier.width(MaterialTheme.spacing.smaller))
                        Text(
                            text = stringResource(R.string.generic_select),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        when (state) {
            SearchScreenViewModel.State.Idle -> { }
            SearchScreenViewModel.State.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is SearchScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is SearchScreenViewModel.State.Success -> {
                LazyColumn(
                    contentPadding = paddingValues + PaddingValues(vertical = MaterialTheme.spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    items(
                        items = state.items,
                        key = { it.hashCode() },
                    ) { searchItem ->
                        SearchResultItem(
                            searchItem = searchItem,
                            selected = selected == searchItem,
                            onClick = { onSelect(searchItem) },
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenContentPreview() {
    PreviewContent {
        SearchScreenContent(
            query = "Tower of God",
            state = SearchScreenViewModel.State.Success(
                items = listOf(
                    SearchResultItem("Tower of God", null, null, null, null, null, emptyMap()),
                ),
                // selected = SearchResultItem("Tower of God", null, null, null, null, null, emptyMap()),
            ),
            selected = null,
            focusRequester = remember { FocusRequester() },
            onBack = { },
            onSearch = { },
            onSelect = { },
            onClickSelect = { },
        )
    }
}
