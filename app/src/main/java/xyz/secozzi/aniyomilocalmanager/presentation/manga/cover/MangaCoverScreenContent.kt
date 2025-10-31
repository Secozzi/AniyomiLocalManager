package xyz.secozzi.aniyomilocalmanager.presentation.manga.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.InfoContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.LoadingContent
import xyz.secozzi.aniyomilocalmanager.presentation.cover.CoverScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.manga.cover.MangaCoverScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun MangaCoverScreenContent(
    state: MangaCoverScreenViewModel.State,
    selectedCover: CoverData?,
    isDownloadingCover: Boolean,
    gridSize: Int,
    result: String?,
    onBack: () -> Unit,
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit,
    onClickDownload: (CoverData) -> Unit,
    onClickCover: (CoverData) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.label_cover)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = onClickSearch) {
                        Icon(Icons.Default.Search, null)
                    }
                    IconButton(onClick = onClickSettings) {
                        Icon(Icons.Default.Settings, null)
                    }
                },
            )
        },
        bottomBar = {
            if (isDownloadingCover) {
                DownloadingBottomBar()
            } else if (selectedCover != null) {
                DownloadBottomBar(onClick = { onClickDownload(selectedCover) })
            }
        },
    ) { contentPadding ->
        // Wait until database is updated, otherwise the idle screen will be briefly shown
        if (result != null || state == MangaCoverScreenViewModel.State.Loading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
            return@Scaffold
        }

        when (state) {
            MangaCoverScreenViewModel.State.Idle -> { }
            MangaCoverScreenViewModel.State.Loading -> {
                throw IllegalStateException("How?")
            }
            is MangaCoverScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            MangaCoverScreenViewModel.State.NoID -> {
                InfoContent(
                    onClick = onClickSearch,
                    icon = Icons.Outlined.Search,
                    subtitle = stringResource(R.string.manga_no_mb_id_set),
                    buttonText = stringResource(R.string.generic_search),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaCoverScreenViewModel.State.Success -> {
                CoverScreenContent(
                    covers = state.data,
                    selectedCover = selectedCover,
                    gridSize = gridSize,
                    onClickCover = onClickCover,
                    paddingValues = contentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DownloadingBottomBar() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(bottom = MaterialTheme.spacing.smaller),
        ) {
            CircularWavyProgressIndicator(
                trackColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(MaterialTheme.spacing.larger),
            )
            Spacer(Modifier.width(MaterialTheme.spacing.smaller))
            Text(
                text = stringResource(R.string.cover_downloading_cover),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun DownloadBottomBar(
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(bottom = MaterialTheme.spacing.smaller),
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.larger),
            )
            Spacer(Modifier.width(MaterialTheme.spacing.smaller))
            Text(
                text = stringResource(R.string.cover_download_cover),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MangaCoverScreenContentTest() {
    PreviewContent {
        val cover2 = CoverData(
            coverUrl = "2",
            origin = "AniList",
            hint = "Volume 2",
        )

        val successState = MangaCoverScreenViewModel.State.Success(
            listOf(
                CoverData(
                    coverUrl = "1",
                    origin = "AniList",
                    hint = "Volume 1",
                ),
                cover2,
                CoverData(
                    coverUrl = "3",
                    origin = "AniList",
                    hint = "Volume 3",
                ),
                CoverData(
                    coverUrl = "4",
                    origin = "AniList",
                    hint = "Volume 4",
                ),
            ),
        )

        MangaCoverScreenContent(
            state = successState,
            selectedCover = cover2,
            isDownloadingCover = false,
            gridSize = 3,
            result = null,
            onBack = { },
            onClickSearch = { },
            onClickSettings = { },
            onClickDownload = { },
            onClickCover = { },
        )
    }
}
