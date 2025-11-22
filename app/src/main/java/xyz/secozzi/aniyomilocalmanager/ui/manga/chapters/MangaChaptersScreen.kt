package xyz.secozzi.aniyomilocalmanager.ui.manga.chapters

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.manga.chapters.MangaChaptersScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect

@Serializable
data class MangaChaptersRoute(val path: String) : NavKey

@Composable
fun MangaChaptersScreen(path: String) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val backStack = LocalBackStack.current

    val viewModel = koinViewModel<MangaChaptersScreenViewModel> {
        parametersOf(path)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val unsaved by viewModel.unsaved.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    CollectAsEffect(viewModel.uiEvent) {
        when (it) {
            is MangaChaptersScreenViewModel.UiEvent.Saved -> {
                val message = if (it.success) {
                    resources.getString(R.string.chapter_edit_save_success)
                } else {
                    resources.getString(R.string.chapter_edit_save_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    MangaChaptersScreenContent(
        state = state,
        name = name,
        unsaved = unsaved,
        loading = loading,
        onBack = { backStack.removeLastOrNull() },
        onEditTitle = viewModel::onEditTitle,
        onEditNumber = viewModel::onEditNumber,
        onEditScanlator = viewModel::onEditScanlator,
        onSave = viewModel::onSave,
    )
}
