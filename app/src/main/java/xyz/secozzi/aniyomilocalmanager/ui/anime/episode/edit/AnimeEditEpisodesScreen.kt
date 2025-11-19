package xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit

import android.widget.Toast
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.edit.AnimeEditEpisodesScreenContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ConfirmDialog
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect

@Serializable
data class AnimeEditEpisodesRoute(val path: String) : NavKey

@Composable
fun AnimeEditEpisodesScreen(path: String) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current
    val resources = LocalResources.current
    val listState = rememberLazyListState()

    val viewModel = koinViewModel<AnimeEditEpisodeScreenViewModel> {
        parametersOf(path)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val validIndexes by viewModel.validIndexes.collectAsStateWithLifecycle()
    val dialog by viewModel.dialog.collectAsStateWithLifecycle()

    val successState = state as? AnimeEditEpisodeScreenViewModel.State.Success
    val expandedState = remember(successState?.data?.size) {
        (successState?.data?.map { false } ?: emptyList()).toMutableStateList()
    }

    CollectAsEffect(viewModel.uiEvent) {
        when (it) {
            is AnimeEditEpisodeScreenViewModel.UiEvent.ScrollTo -> {
                listState.animateScrollToItem(it.index)
            }
            is AnimeEditEpisodeScreenViewModel.UiEvent.Downloaded -> {
                val message = if (it.success) {
                    resources.getString(R.string.episode_save_details_success)
                } else {
                    resources.getString(R.string.episode_save_details_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AnimeEditEpisodesScreenContent(
        state = state,
        name = name,
        lazyListState = listState,
        validIndexes = validIndexes,
        expandedState = expandedState.toPersistentList(),
        onExpandedStateChange = { expandedState[it] = !expandedState[it] },
        onEditNumber = viewModel::onEditNumber,
        onEditTitle = viewModel::onEditTitle,
        onEditFiller = viewModel::onEditFiller,
        onEditDescription = viewModel::onEditDescription,
        onEditScanlator = viewModel::onEditScanlator,
        onEditPreviewUrl = viewModel::onEditPreviewUrl,
        onEditDate = viewModel::onEditDate,
        onDelete = viewModel::showDeleteDialog,
        onSave = viewModel::save,
        onAdd = viewModel::onAdd,
        onBack = { backStack.removeLastOrNull() },
    )

    when (dialog) {
        is AnimeEditEpisodeScreenViewModel.Dialog.ConfirmDelete -> {
            ConfirmDialog(
                onConfirm = {
                    viewModel.onDelete((dialog as AnimeEditEpisodeScreenViewModel.Dialog.ConfirmDelete).index)
                    viewModel.dismissDialog()
                },
                onDismiss = { viewModel.dismissDialog() },
            )
        }
        null -> { }
    }
}
