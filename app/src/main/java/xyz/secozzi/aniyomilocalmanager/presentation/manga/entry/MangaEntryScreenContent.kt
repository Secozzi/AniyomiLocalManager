package xyz.secozzi.aniyomilocalmanager.presentation.manga.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.BookOnline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun MangaEntryScreenContent(
    state: MangaEntryScreenViewModel.State,
    detailsState: MangaEntryScreenViewModel.DetailsInfo?,
    name: String,
    onBack: () -> Unit,
    onEditCover: () -> Unit,
    onEditComicInfo: () -> Unit,
    onEditChapters: () -> Unit,
    onClickMangaBaka: () -> Unit,
    onClickAnilist: () -> Unit,
    onClickMal: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
            )
        },
    ) { contentPadding ->
        when (state) {
            MangaEntryScreenViewModel.State.Idle -> {}
            is MangaEntryScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaEntryScreenViewModel.State.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = contentPadding,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    // Items
                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 0,
                            headlineContent = { Text(text = stringResource(R.string.edit_cover)) },
                            leadingContent = {
                                when {
                                    detailsState == null -> {
                                        CircularProgressIndicator(
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(MaterialTheme.spacing.medium),
                                        )
                                    }
                                    detailsState.hasCover -> {
                                        Icon(Icons.Default.Check, null)
                                    }
                                    !detailsState.hasCover -> {
                                        Icon(Icons.Default.Clear, null)
                                    }
                                }
                            },
                            onClick = onEditCover,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 1,
                            headlineContent = { Text(text = stringResource(R.string.manga_edit_comicinfo)) },
                            leadingContent = {
                                when {
                                    detailsState == null -> {
                                        CircularProgressIndicator(
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(MaterialTheme.spacing.medium),
                                        )
                                    }
                                    detailsState.hasComicInfo -> {
                                        Icon(Icons.Default.Check, null)
                                    }
                                    !detailsState.hasComicInfo -> {
                                        Icon(Icons.Default.Clear, null)
                                    }
                                }
                            },
                            onClick = onEditComicInfo,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 2,
                            headlineContent = { Text(text = stringResource(R.string.manga_edit_chapter_comicinfo)) },
                            leadingContent = { Icon(Icons.AutoMirrored.Filled.List, null) },
                            onClick = onEditChapters,
                        )
                    }

                    item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

                    // Ids
                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 0,
                            headlineContent = { Text(text = stringResource(R.string.pref_mangabaka_title)) },
                            leadingContent = { Icon(Icons.Outlined.BookOnline, null) },
                            supportingContent = {
                                Text(
                                    text = state.data.mangabaka?.let {
                                        stringResource(R.string.generic_id, it)
                                    } ?: stringResource(R.string.no_id_set),
                                )
                            },
                            onClick = onClickMangaBaka,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 1,
                            headlineContent = { Text(text = stringResource(R.string.pref_anilist_title)) },
                            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.anilist_icon), null) },
                            supportingContent = {
                                Text(
                                    text = state.data.anilist?.let {
                                        stringResource(R.string.generic_id, it)
                                    } ?: stringResource(R.string.no_id_set),
                                )
                            },
                            onClick = onClickAnilist,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 2,
                            headlineContent = { Text(text = stringResource(R.string.pref_mal_title)) },
                            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.mal_icon), null) },
                            supportingContent = {
                                Text(
                                    text = state.data.mal?.let {
                                        stringResource(R.string.generic_id, it)
                                    } ?: stringResource(R.string.no_id_set),
                                )
                            },
                            onClick = onClickMal,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun MangaEntryScreenContentPreview() {
    PreviewContent {
        MangaEntryScreenContent(
            state = MangaEntryScreenViewModel.State.Success(
                data = MangaTrackerEntity(
                    path = "",
                    anilist = 1234L,
                    mal = 54321L,
                ),
            ),
            detailsState = null,
            name = "Boku no Hero Academia",
            onBack = {},
            onEditCover = {},
            onEditComicInfo = {},
            onEditChapters = {},
            onClickMangaBaka = {},
            onClickAnilist = {},
            onClickMal = {},
        )
    }
}
