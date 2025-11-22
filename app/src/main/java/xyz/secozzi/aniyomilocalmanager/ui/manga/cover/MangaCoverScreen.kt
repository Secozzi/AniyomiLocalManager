package xyz.secozzi.aniyomilocalmanager.ui.manga.cover

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsStateWithLifecycle
import xyz.secozzi.aniyomilocalmanager.presentation.manga.cover.MangaCoverScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.preferences.CoverPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchResult
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.CollectAsEffect
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore

@Serializable
data class MangaCoverRoute(val path: String) : NavKey

@Composable
fun MangaCoverScreen(path: String) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val backStack = LocalBackStack.current
    val resultStore = LocalResultStore.current

    val coverPreferences = koinInject<CoverPreferences>()

    val viewModel = koinViewModel<MangaCoverScreenViewModel> {
        parametersOf(path)
    }

    val gridSize by coverPreferences.gridSize.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val isDownloadingCover by viewModel.isDownloadingCover.collectAsStateWithLifecycle()
    val selectedCover by viewModel.selectedCover.collectAsStateWithLifecycle()

    val result: SearchResult? = resultStore.getResultState<SearchResult>()
    LaunchedEffect(result) {
        if (result == null) return@LaunchedEffect
        resultStore.removeResult<SearchResult>()
        viewModel.updateIds(result)
    }

    CollectAsEffect(viewModel.toastEvent) {
        when (it) {
            is MangaCoverScreenViewModel.ToastEvent.Downloaded -> {
                val message = if (it.success) {
                    resources.getString(R.string.cover_download_cover_success)
                } else {
                    resources.getString(R.string.cover_download_cover_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    MangaCoverScreenContent(
        state = state,
        selectedCover = selectedCover,
        isDownloadingCover = isDownloadingCover,
        gridSize = gridSize,
        result = result,
        onBack = { backStack.removeLastOrNull() },
        onClickSearch = { backStack.add(SearchRoute(name, SearchIds.MangaBaka)) },
        onClickSettings = { backStack.add(CoverPreferencesRoute) },
        onClickDownload = viewModel::downloadCover,
        onClickCover = viewModel::selectCover,
    )
}
