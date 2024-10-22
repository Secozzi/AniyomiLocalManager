package xyz.secozzi.aniyomilocalmanager.ui.entry.manga.comicinfo

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
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
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALManga
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.domain.model.Status
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.EditableDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.SimpleDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.DetailsScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.entry.DetailsScreenModel
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniListPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class ComicInfoScreen(val path: String) : Screen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val clipboardManager = LocalClipboardManager.current

        val fileManager = koinInject<FileManager>()
        val xml = koinInject<XML>()

        val screenModel = rememberScreenModel(tag = "manga") {
            DetailsScreenModel(path, fileManager, xml)
        }

        val titles by screenModel.titles.collectAsState()
        val title by screenModel.title.collectAsState()
        val author by screenModel.author.collectAsState()
        val artist by screenModel.artist.collectAsState()
        val description by screenModel.description.collectAsState()
        val genre by screenModel.genre.collectAsState()
        val status by screenModel.status.collectAsState()

        val result = getResult().value as? ALManga
        if (result != null) {
            screenModel.updateManga(result)
            navigator.clearResults()
        }

        DetailsScreenContent(
            title = stringResource(R.string.entry_comicinfo_title),
            generateText = stringResource(R.string.entry_comicinfo_generate),
            onBack = { navigator.pop() },
            onSearch = {
                navigator.push(
                    SearchScreen(
                        searchQuery = path.getDirectoryName(),
                        searchRepositoryId = SearchRepositoryManager.ANILIST_MANGA,
                    )
                )
           },
            onSettings = { navigator.push(AniListPreferencesScreen) },
            onGenerate = {
                val generateResult = screenModel.generateComicInfoXml()

                val message = if (generateResult) {
                    context.resources.getString(R.string.entry_generate_comicinfo_success)
                } else {
                    context.resources.getString(R.string.entry_generate_comicinfo_failure)
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onCopy = {
                clipboardManager.setText(AnnotatedString(screenModel.generateComicInfoXmlString()))
            },
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
                label = { Text(text = stringResource(R.string.entry_author_label)) },
                modifier = maxWidth,
                singleLine = true,
            )

            OutlinedTextField(
                value = artist,
                onValueChange = screenModel::updateArtist,
                label = { Text(text = stringResource(R.string.entry_artist_label)) },
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
