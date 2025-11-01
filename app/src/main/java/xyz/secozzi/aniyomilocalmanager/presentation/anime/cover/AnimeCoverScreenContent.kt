package xyz.secozzi.aniyomilocalmanager.presentation.anime.cover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.InfoContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.LoadingContent
import xyz.secozzi.aniyomilocalmanager.presentation.cover.CoverScreenContent
import xyz.secozzi.aniyomilocalmanager.presentation.cover.DownloadBottomBar
import xyz.secozzi.aniyomilocalmanager.presentation.cover.DownloadingBottomBar
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverScreenViewModel

@Composable
fun AnimeCoverScreenContent(
    state: AnimeCoverScreenViewModel.State,
    selectedCover: CoverData?,
    isDownloadingCover: Boolean,
    gridSize: Int,
    result: SearchResultItem?,
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
        if (result != null || state == AnimeCoverScreenViewModel.State.Loading) {
            LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
            return@Scaffold
        }

        when (state) {
            AnimeCoverScreenViewModel.State.Idle -> { }
            AnimeCoverScreenViewModel.State.Loading -> {
                throw IllegalStateException("How?")
            }
            is AnimeCoverScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            AnimeCoverScreenViewModel.State.NoID -> {
                InfoContent(
                    onClick = onClickSearch,
                    icon = Icons.Outlined.Search,
                    subtitle = stringResource(R.string.entry_no_id_set),
                    buttonText = stringResource(R.string.generic_search),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeCoverScreenViewModel.State.Success -> {
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
