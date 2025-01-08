package xyz.secozzi.aniyomilocalmanager.ui.entry.manga.cover

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import okhttp3.OkHttpClient
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALManga
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverRepository
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.NotSearchedContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ProgressContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.cover.CoverResultList
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.CoverScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.preferences.CoverPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class MangaCoverScreen(val path: String, val anilistId: Long?) : Screen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val coverRepo = koinInject<CoverRepository>()
        val fileManager = koinInject<FileManager>()
        val client = koinInject<OkHttpClient>()
        val preferences = koinInject<CoverPreferences>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()

        val screenModel = rememberScreenModel {
            MangaCoverScreenModel(path, coverRepo, fileManager, client, trackerIdRepository)
        }

        val gridSize by preferences.gridSize.collectAsState()
        val selectedCover by screenModel.selectedCover.collectAsState()
        val state by screenModel.state.collectAsState()

        val result = getResult().value as? ALManga
        if (result != null) {
            screenModel.updateAniList(result.remoteId)
            screenModel.getCovers(result.remoteId)
            navigator.clearResults()
        } else if (anilistId != null) {
            screenModel.getCovers(anilistId)
        }

        CoverScreenContent(
            onBack = { navigator.pop() },
            onSearch = {
                navigator.push(
                    SearchScreen(
                        searchQuery = path.getDirectoryName(),
                        searchRepositoryId = SearchRepositoryManager.ANILIST_MANGA,
                    ),
                )
            },
            onSettings = { navigator.push(CoverPreferencesScreen) },
            onDownloadCover = {
                val downloadResult = screenModel.downloadCover()

                val message = if (downloadResult) {
                    context.resources.getString(R.string.cover_download_cover_success)
                } else {
                    context.resources.getString(R.string.cover_download_cover_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            hasSelected = selectedCover != null,
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            state.DisplayResult(
                onIdle = {
                    if (anilistId == null) {
                        NotSearchedContent(
                            title = stringResource(R.string.cover_not_searched),
                            modifier = paddingModifier,
                            onSearch = {
                                navigator.push(
                                    SearchScreen(
                                        searchQuery = path.getDirectoryName(),
                                        searchRepositoryId = SearchRepositoryManager.ANILIST_MANGA,
                                    ),
                                )
                            },
                        )
                    } else {
                        ProgressContent(modifier = paddingModifier)
                    }
                },
                onLoading = { ProgressContent(modifier = paddingModifier) },
                onError = { ErrorContent(it, modifier = paddingModifier) },
                onSuccess = { values ->
                    CoverResultList(
                        values = values,
                        selectedCover = selectedCover,
                        paddingValues = paddingValues,
                        gridSize = gridSize,
                        onItemClick = screenModel::updateSelected,
                    )
                },
            )
        }
    }
}
