package xyz.secozzi.aniyomilocalmanager.presentation.anime.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun AnimeEntryScreenContent(
    state: AnimeEntryScreenViewModel.State,
    name: String,
    onBack: () -> Unit,
    onEditCover: () -> Unit,
    onEditDetails: () -> Unit,
    onEditEpisodes: () -> Unit,
    onClickAnilist: () -> Unit,
    onClickAnidb: () -> Unit,
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
            AnimeEntryScreenViewModel.State.Idle -> { }
            is AnimeEntryScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeEntryScreenViewModel.State.Success -> {
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
                                Icon(if (state.hasCover) Icons.Default.Check else Icons.Default.Clear, null)
                            },
                            onClick = onEditCover,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 1,
                            headlineContent = { Text(text = stringResource(R.string.anime_edit_details)) },
                            leadingContent = {
                                Icon(if (state.hasDetails) Icons.Default.Check else Icons.Default.Clear, null)
                            },
                            onClick = onEditDetails,
                        )
                    }

                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 2,
                            headlineContent = { Text(text = stringResource(R.string.anime_edit_episodes)) },
                            leadingContent = {
                                Icon(if (state.hasDetails) Icons.Default.Check else Icons.Default.Clear, null)
                            },
                            onClick = onEditEpisodes,
                        )
                    }

                    item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

                    // Ids
                    item {
                        ExpressiveListItem(
                            itemSize = 3,
                            index = 0,
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
                            index = 1,
                            headlineContent = { Text(text = stringResource(R.string.pref_anidb_title)) },
                            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.anidb_icon), null) },
                            supportingContent = {
                                Text(
                                    text = state.data.anidb?.let {
                                        stringResource(R.string.generic_id, it)
                                    } ?: stringResource(R.string.no_id_set),
                                )
                            },
                            onClick = onClickAnidb,
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

@PreviewLightDark
@Composable
private fun AnimeEntryScreenContentPreview() {
    PreviewContent {
        AnimeEntryScreenContent(
            state = AnimeEntryScreenViewModel.State.Success(
                hasCover = false,
                hasDetails = true,
                hasEpisodes = true,
                data = AnimeTrackerEntity(
                    path = "",
                    anilist = 1234L,
                    mal = 54321L,
                ),
            ),
            name = "Boku no Hero Academia",
            onBack = {},
            onEditCover = {},
            onEditDetails = {},
            onEditEpisodes = {},
            onClickAnilist = {},
            onClickAnidb = {},
            onClickMal = {},
        )
    }
}
