package xyz.secozzi.aniyomilocalmanager.presentation.home.manga

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.home.MangaListEntry
import xyz.secozzi.aniyomilocalmanager.presentation.components.InfoContent
import xyz.secozzi.aniyomilocalmanager.presentation.home.list.MangaListEntry
import xyz.secozzi.aniyomilocalmanager.ui.home.manga.MangaScreenViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MangaScreenContent(
    state: MangaScreenViewModel.State,
    isLoading: Boolean,
    bottomPadding: Dp,
    onClickSelectLocation: () -> Unit,
    onClickSettings: () -> Unit,
    onClickItem: (MangaListEntry) -> Unit,
) {
    Scaffold(
        modifier = Modifier.padding(bottom = bottomPadding),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.label_manga)) },
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
            MangaScreenViewModel.State.Idle -> {}
            MangaScreenViewModel.State.Unset -> {
                InfoContent(
                    onClick = onClickSelectLocation,
                    icon = Icons.Outlined.Folder,
                    subtitle = stringResource(R.string.no_location_set_manga),
                    buttonText = stringResource(R.string.generic_select),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaScreenViewModel.State.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                ) {
                    itemsIndexed(state.entries) { index, item ->
                        MangaListEntry(
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
