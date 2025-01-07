package xyz.secozzi.aniyomilocalmanager.ui.entry.manga

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
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALManga
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.EntryScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.entry.SelectItem
import xyz.secozzi.aniyomilocalmanager.ui.entry.TrackerIdItem
import xyz.secozzi.aniyomilocalmanager.ui.entry.manga.comicinfo.ComicInfoScreen
import xyz.secozzi.aniyomilocalmanager.ui.entry.manga.cover.MangaCoverScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class MangaEntryScreen(val path: String) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val fileManager = koinInject<FileManager>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()
        val screenModel = rememberScreenModel { MangaEntryScreenModel(path, fileManager, trackerIdRepository) }

        val anilistId by screenModel.anilistId.collectAsState()

        val result = getResult().value as? ALManga
        if (result != null) {
            screenModel.updateDatabase(result.remoteId)
            navigator.clearResults()
        }

        EntryScreenContent(
            path = path,
            onBack = { navigator.pop() }
        ) {
            SelectItem(
                title = stringResource(R.string.entry_create_cover),
                present = screenModel.hasCover(),
                onClick = { navigator.push(MangaCoverScreen(path, anilistId)) },
            )

            SelectItem(
                title = stringResource(R.string.entry_create_comicinfo),
                present = screenModel.hasComicInfo(),
                onClick = { navigator.push(ComicInfoScreen(path, anilistId)) },
            )

            Spacer(modifier = Modifier.weight(1f))

            TrackerIdItem(
                title = stringResource(R.string.entry_item_tracker_anilist),
                trackerId = anilistId,
                icon = { Icon(ImageVector.vectorResource(R.drawable.anilist_icon), null ) },
                onClick = {
                    navigator.push(
                        SearchScreen(
                            searchQuery = path.getDirectoryName(),
                            searchRepositoryId = SearchRepositoryManager.ANILIST_MANGA,
                        )
                    )
                },
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
        }
    }
}
