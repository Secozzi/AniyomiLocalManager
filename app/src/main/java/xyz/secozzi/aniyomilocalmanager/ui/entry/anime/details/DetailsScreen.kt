package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.details

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearch
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALAnime
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.domain.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.EditableDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ProgressContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.SimpleDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.DetailsScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.entry.DetailsScreenModel
import xyz.secozzi.aniyomilocalmanager.ui.home.tabs.ANIME_DIRECTORY_NAME
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniListPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class DetailsScreen(val path: String, val anilistId: Long?) : Screen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val clipboardManager = LocalClipboardManager.current

        val fileManager = koinInject<FileManager>()
        val xml = koinInject<XML>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()
        val anilistSearch = koinInject<AnilistSearch>()
        val anilistPreferences = koinInject<AniListPreferences>()

        val screenModel = rememberScreenModel(tag = "anime") {
            DetailsScreenModel(path, anilistId, AnilistSearchType.ANIME, fileManager, xml, trackerIdRepository, anilistSearch, anilistPreferences)
        }

        val state by screenModel.state.collectAsState()

        val titles by screenModel.titles.collectAsState()
        val title by screenModel.title.collectAsState()
        val author by screenModel.author.collectAsState()
        val artist by screenModel.artist.collectAsState()
        val description by screenModel.description.collectAsState()
        val genre by screenModel.genre.collectAsState()
        val status by screenModel.status.collectAsState()

        val result = getResult().value as? ALAnime
        if (result != null) {
            screenModel.updateAnime(result)
            screenModel.updateAniList(ANIME_DIRECTORY_NAME, result.remoteId)
            navigator.clearResults()
        }

        DetailsScreenContent(
            title = stringResource(R.string.entry_details_title),
            generateText = stringResource(R.string.entry_details_generate),
            onBack = { navigator.pop() },
            onSearch = {
                navigator.push(
                    SearchScreen(
                        searchQuery = path.getDirectoryName(),
                        searchRepositoryId = SearchRepositoryManager.ANILIST_ANIME,
                    )
                )
            },
            onSettings = { navigator.push(AniListPreferencesScreen) },
            onGenerate = {
                val generateResult = screenModel.generateDetailsJson()

                val message = if (generateResult) {
                    context.resources.getString(R.string.entry_generate_details_success)
                } else {
                    context.resources.getString(R.string.entry_generate_details_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onCopy = {
                clipboardManager.setText(AnnotatedString(screenModel.generateJsonString()))
            },
        ) { paddingValues ->
            when {
                anilistId != null && (state !is DetailsScreenModel.State.Finished) -> {
                    ProgressContent(modifier = Modifier.padding(paddingValues))
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                            ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller)
                    ) {
                        val maxWidth = Modifier.fillMaxWidth()

                        EditableDropdown(
                            value = title,
                            label = stringResource(R.string.entry_title_label),
                            values = titles,
                            onValueChange = screenModel::updateTitle,
                        )

                        OutlinedTextField(
                            value = author,
                            onValueChange = screenModel::updateAuthor,
                            label = { Text(text = stringResource(R.string.entry_studio_label)) },
                            modifier = maxWidth,
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = artist,
                            onValueChange = screenModel::updateArtist,
                            label = { Text(text = stringResource(R.string.entry_fansub_label)) },
                            modifier = maxWidth,
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = screenModel::updateDescription,
                            label = { Text(text = stringResource(R.string.entry_description_label)) },
                            modifier = maxWidth,
                            minLines = 3,
                        )

                        OutlinedTextField(
                            value = genre,
                            onValueChange = screenModel::updateGenre,
                            label = { Text(text = stringResource(R.string.entry_genre_label)) },
                            modifier = maxWidth,
                            singleLine = true,
                            supportingText = {
                                Text(text = stringResource(R.string.entry_genre_supporting_text))
                            },
                        )

                        SimpleDropdown(
                            label = stringResource(R.string.entry_item_status),
                            selectedItem = status,
                            items = Status.entries,
                            modifier = maxWidth,
                            onSelected = screenModel::updateStatus,
                        )
                    }
                }
            }
        }
    }
}
