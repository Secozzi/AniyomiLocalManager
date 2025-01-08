package xyz.secozzi.aniyomilocalmanager.ui.entry.anime

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.anidb.search.dto.ADBAnime
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALAnime
import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.EntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.entry.SelectItem
import xyz.secozzi.aniyomilocalmanager.ui.entry.TrackerIdItem
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.cover.AnimeCoverScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.details.DetailsScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EpisodeScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class AnimeEntryScreen(val path: String) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val fileManager = koinInject<FileManager>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()
        val screenModel = rememberScreenModel { AnimeEntryScreenModel(path, fileManager, trackerIdRepository) }

        val anilistId by screenModel.anilistId.collectAsState()
        val aniDBId by screenModel.anidbId.collectAsState()

        val result = getResult().value as? SearchDataItem
        if (result != null && result is ALAnime) {
            screenModel.updateAniList(anilistId = result.remoteId)
            navigator.clearResults()
        } else if (result != null && result is ADBAnime) {
            screenModel.updateAniDB(aniDBId = result.remoteId)
            navigator.clearResults()
        }

        EntryScreenContent(
            path = path,
            onBack = { navigator.pop() },
        ) {
            SelectItem(
                title = stringResource(R.string.entry_create_cover),
                present = screenModel.hasCover(),
                onClick = { navigator.push(AnimeCoverScreen(path, anilistId)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_details),
                present = screenModel.hasDetailsJson(),
                onClick = { navigator.push(DetailsScreen(path, anilistId)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_episodes),
                present = screenModel.hasEpisodesJson(),
                onClick = { navigator.push(EpisodeScreen(path, aniDBId)) },
            )

            Spacer(modifier = Modifier.weight(1f))

            TrackerIdItem(
                title = stringResource(R.string.entry_item_tracker_anilist),
                trackerId = anilistId,
                icon = { Icon(ImageVector.vectorResource(R.drawable.anilist_icon), null) },
                onClick = {
                    navigator.push(
                        SearchScreen(
                            searchQuery = path.getDirectoryName(),
                            searchRepositoryId = SearchRepositoryManager.ANILIST_ANIME,
                        ),
                    )
                },
            )

            TrackerIdItem(
                title = stringResource(R.string.entry_item_tracker_anidb),
                trackerId = aniDBId,
                icon = { Icon(ImageVector.vectorResource(R.drawable.anidb_icon), null) },
                onClick = {
                    navigator.push(
                        SearchScreen(
                            searchQuery = path.getDirectoryName(),
                            searchRepositoryId = SearchRepositoryManager.ANIDB,
                        ),
                    )
                },
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
        }
    }
}
