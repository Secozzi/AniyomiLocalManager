package xyz.secozzi.aniyomilocalmanager.ui.manga.details

import android.content.ClipData
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.manga.details.MangaDetailsScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect

@Serializable
data class MangaDetailsRoute(val path: String) : NavKey

@Composable
fun MangaDetailsScreen(path: String) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val backStack = LocalBackStack.current
    val clipboard = LocalClipboard.current

    val viewModel = koinViewModel<MangaDetailsScreenViewModel> {
        parametersOf(path)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedSearch by viewModel.selectedSearch.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    CollectAsEffect(viewModel.uiEvent) {
        when (it) {
            is MangaDetailsScreenViewModel.UiEvent.Downloaded -> {
                val message = if (it.success) {
                    resources.getString(R.string.details_generate_comicinfo_success)
                } else {
                    resources.getString(R.string.details_generate_comicinfo_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            is MangaDetailsScreenViewModel.UiEvent.Copy -> {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(it.text, it.text)))
            }
        }
    }

    MangaDetailsScreenContent(
        onBack = { backStack.removeLastOrNull() },
        state = state,
        selectedSearch = selectedSearch,
        isLoading = isLoading,
        onEditTitle = viewModel::updateTitle,
        onEditAuthor = viewModel::updateAuthor,
        onEditArtist = viewModel::updateArtist,
        onEditDescription = viewModel::updateDescription,
        onEditGenre = viewModel::updateGenre,
        onEditStatus = viewModel::updateStatus,
        onClickSearchId = viewModel::updateSelectedSearchId,
        onDownload = viewModel::generateComicInfo,
        onCopy = viewModel::copyComicInfo,
    )
}
