package xyz.secozzi.aniyomilocalmanager.presentation.anime.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.TopLoadingIndicator
import xyz.secozzi.aniyomilocalmanager.presentation.components.details.DetailsScreenContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.details.GenerateBottomBar
import xyz.secozzi.aniyomilocalmanager.ui.anime.details.AnimeDetailsScreenViewModel

@Composable
fun AnimeDetailsScreenContent(
    onBack: () -> Unit,
    state: AnimeDetailsScreenViewModel.State,
    selectedSearch: SearchIds?,
    isLoading: Boolean,
    onEditTitle: (String) -> Unit,
    onEditAuthor: (String) -> Unit,
    onEditArtist: (String) -> Unit,
    onEditDescription: (String) -> Unit,
    onEditGenre: (String) -> Unit,
    onEditStatus: (Status) -> Unit,
    onClickSearchId: (SearchIds?) -> Unit,
    onDownload: () -> Unit,
    onCopy: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val dropdownItems = remember {
        listOf(
            null to R.string.details_json_name,
            SearchIds.AnilistAnime to R.string.pref_anilist_title,
            SearchIds.MalAnime to R.string.pref_mal_title,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    SearchIcon(selectedSearch)

                    IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                        Icon(if (isMenuExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, null)
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        dropdownItems.forEach { (id, stringRes) ->
                            DropdownMenuItem(
                                text = { Text(text = stringResource(stringRes)) },
                                onClick = {
                                    onClickSearchId(id)
                                    isMenuExpanded = false
                                },
                                leadingIcon = { SearchIcon(id) },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            GenerateBottomBar(
                label = "Generate details.json",
                onGenerate = onDownload,
                onCopy = onCopy,
            )
        },
    ) { contentPadding ->
        TopLoadingIndicator(
            isLoading = isLoading,
            contentPadding = contentPadding,
        )

        when (state) {
            AnimeDetailsScreenViewModel.State.Idle -> { }
            is AnimeDetailsScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.throwable,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeDetailsScreenViewModel.State.Success -> {
                DetailsScreenContent(
                    contentPadding = contentPadding,
                    details = state.details,
                    authorLabel = stringResource(R.string.details_edit_studio),
                    artistLabel = stringResource(R.string.details_edit_fansub),
                    onEditTitle = onEditTitle,
                    onEditAuthor = onEditAuthor,
                    onEditArtist = onEditArtist,
                    onEditDescription = onEditDescription,
                    onEditGenre = onEditGenre,
                    onEditStatus = onEditStatus,
                )
            }
        }
    }
}

@Composable
private fun SearchIcon(searchIds: SearchIds?) {
    val imageVector = when (searchIds) {
        SearchIds.MangaBaka -> Icons.Default.Book
        SearchIds.AnilistAnime, SearchIds.AnilistManga -> ImageVector.vectorResource(R.drawable.anilist_icon)
        SearchIds.MalAnime, SearchIds.MalManga -> ImageVector.vectorResource(R.drawable.mal_icon)
        null -> Icons.Default.Folder
    }

    Icon(imageVector, null)
}

@Composable
@PreviewLightDark
private fun AnimeDetailsScreenContentPreview() {
    PreviewContent {
        AnimeDetailsScreenContent(
            onBack = { },
            state = AnimeDetailsScreenViewModel.State.Idle,
            selectedSearch = SearchIds.AnilistAnime,
            isLoading = false,
            onEditTitle = { },
            onEditAuthor = { },
            onEditArtist = { },
            onEditDescription = { },
            onEditGenre = { },
            onEditStatus = { },
            onClickSearchId = { },
            onDownload = { },
            onCopy = { },
        )
    }
}
