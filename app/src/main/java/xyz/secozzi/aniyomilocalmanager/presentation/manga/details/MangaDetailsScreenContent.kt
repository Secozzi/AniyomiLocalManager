package xyz.secozzi.aniyomilocalmanager.presentation.manga.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.FloatingBottomBar
import xyz.secozzi.aniyomilocalmanager.presentation.components.TopLoadingIndicator
import xyz.secozzi.aniyomilocalmanager.presentation.components.details.DetailsScreenContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.details.SearchIcon
import xyz.secozzi.aniyomilocalmanager.ui.manga.details.MangaDetailsScreenViewModel

@Composable
fun MangaDetailsScreenContent(
    onBack: () -> Unit,
    state: MangaDetailsScreenViewModel.State,
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
    val successState = state as? MangaDetailsScreenViewModel.State.Success
    var isMenuExpanded by remember { mutableStateOf(false) }

    val dropdownItems = remember(successState) {
        buildList(4) {
            add(null to R.string.details_comicinfo_name)
            successState?.searchIds?.keys?.forEach {
                add(it to it.stringRes)
            }
        }
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
            FloatingBottomBar(
                label = stringResource(R.string.details_generate_comicinfo),
                onGenerate = onDownload,
                onSecondary = onCopy,
            )
        },
    ) { contentPadding ->
        TopLoadingIndicator(
            isLoading = isLoading,
            contentPadding = contentPadding,
        )

        when (state) {
            MangaDetailsScreenViewModel.State.Idle -> { }
            is MangaDetailsScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.throwable,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaDetailsScreenViewModel.State.Success -> {
                DetailsScreenContent(
                    contentPadding = contentPadding,
                    details = state.details,
                    authorLabel = stringResource(R.string.details_edit_author),
                    artistLabel = stringResource(R.string.details_edit_artist),
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
@PreviewLightDark
private fun MangaDetailsScreenContentPreview() {
    PreviewContent {
        MangaDetailsScreenContent(
            onBack = { },
            state = MangaDetailsScreenViewModel.State.Idle,
            selectedSearch = SearchIds.AnilistManga,
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
