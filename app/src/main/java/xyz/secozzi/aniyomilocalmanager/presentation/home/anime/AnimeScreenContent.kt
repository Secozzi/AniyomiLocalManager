package xyz.secozzi.aniyomilocalmanager.presentation.home.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.home.AnimeListEntry
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.home.UnsetContent
import xyz.secozzi.aniyomilocalmanager.presentation.home.list.AnimeListEntry
import xyz.secozzi.aniyomilocalmanager.ui.home.anime.AnimeScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimeScreenContent(
    state: AnimeScreenViewModel.State,
    isLoading: Boolean,
    bottomPadding: Dp,
    onClickSelectLocation: () -> Unit,
    onClickSettings: () -> Unit,
    onNavigateTo: (Int) -> Unit,
    onClickItem: (AnimeListEntry) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(bottom = bottomPadding),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.label_anime)) },
                actions = {
                    IconButton(onClick = onClickSettings) {
                        Icon(Icons.Default.Settings, null)
                    }
                },
            )
        },
    ) { contentPadding ->
        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier.zIndex(1f),
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300),
            ) + fadeIn(tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300),
            ) + fadeOut(tween(300)),
        ) {
            Box(modifier = Modifier.padding(contentPadding).fillMaxWidth()) {
                ContainedLoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        }

        when (state) {
            AnimeScreenViewModel.State.Idle -> {}
            AnimeScreenViewModel.State.Unset -> {
                UnsetContent(
                    onClick = onClickSelectLocation,
                    subtitle = stringResource(R.string.no_location_set_anime),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeScreenViewModel.State.Success -> {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                ) {
                    PathLevelIndication(
                        pathList = state.relative,
                        onNavigateTo = onNavigateTo,
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        itemsIndexed(state.entries) { index, item ->
                            AnimeListEntry(
                                itemSize = state.entries.size,
                                index = index,
                                entry = item,
                                onClick = { onClickItem(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AnimeScreenContentPreview() {
    PreviewContent {
        val successState = AnimeScreenViewModel.State.Success(
            entries = listOf(
                AnimeListEntry(
                    isSeason = false,
                    name = "Astra.Lost.in.Space.S01.1080p.BluRay.Opus2.0.H.264-LYS1TH3A",
                    lastModified = "29/10/2024 - 10:07:22",
                    size = 5,
                    hasCover = true,
                    hasDetails = false,
                    hasEpisodes = true,
                ),
            ),
            relative = listOf("localanime", "Season 1"),
        )
        val unsetState = AnimeScreenViewModel.State.Unset

        AnimeScreenContent(
            state = unsetState,
            isLoading = false,
            bottomPadding = 0.dp,
            onClickSelectLocation = {},
            onClickSettings = {},
            onNavigateTo = {},
            onClickItem = {},
        )
    }
}
