package xyz.secozzi.aniyomilocalmanager.presentation.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.DropdownItem
import xyz.secozzi.aniyomilocalmanager.presentation.components.SimpleDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.utils.plus
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun DetailsScreenContent(
    contentPadding: PaddingValues,
    details: EntryDetails,
    authorLabel: String,
    artistLabel: String,
    onEditTitle: (String) -> Unit,
    onEditAuthor: (String) -> Unit,
    onEditArtist: (String) -> Unit,
    onEditDescription: (String) -> Unit,
    onEditGenre: (String) -> Unit,
    onEditStatus: (Status) -> Unit,
) {
    LazyColumn(
        contentPadding = contentPadding + PaddingValues(
            start = MaterialTheme.spacing.medium,
            end = MaterialTheme.spacing.medium,
            bottom = MaterialTheme.spacing.smaller,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
    ) {
        item {
            EditableDropdownMenu(
                value = details.title,
                label = stringResource(R.string.details_edit_title),
                values = details.titles,
                onValueChange = onEditTitle,
            )
        }

        item {
            OutlinedTextField(
                value = details.authors,
                onValueChange = onEditAuthor,
                label = { Text(text = authorLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        item {
            OutlinedTextField(
                value = details.artists,
                onValueChange = onEditArtist,
                label = { Text(text = artistLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        item {
            OutlinedTextField(
                value = details.description,
                onValueChange = onEditDescription,
                label = { Text(text = stringResource(R.string.details_edit_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )
        }

        item {
            OutlinedTextField(
                value = details.genre,
                onValueChange = onEditGenre,
                label = { Text(text = stringResource(R.string.details_edit_genre)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text(text = stringResource(R.string.details_edit_genre_summary)) },
            )
        }

        item {
            SimpleDropdown(
                label = stringResource(R.string.details_edit_status),
                selected = details.status.toDropdownItem(),
                items = Status.entries.map { it.toDropdownItem() }.toPersistentList(),
                onSelected = onEditStatus,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun Status.toDropdownItem(): DropdownItem<Status> {
    return DropdownItem(
        item = this,
        displayName = stringResource(this.stringRes),
        extraData = null,
    )
}

@Composable
@PreviewLightDark
private fun DetailsScreenContentPreview() {
    PreviewContent {
        DetailsScreenContent(
            contentPadding = PaddingValues(),
            details = EntryDetails(
                title = "Boku no Hero Academia",
                titles = persistentListOf(
                    "Boku no Hero Academia",
                    "My Hero Academia",
                    "僕のヒーローアカデミア",
                ),
                authors = "bones",
                artists = "",
                description = """What would the world be like if 80 percent of the population manifested extraordinary superpowers called “Quirks” at age four? Heroes and villains would be battling it out everywhere! Becoming a hero would mean learning to use your power, but where would you go to study? U.A. High's Hero Program of course! But what would you do if you were one of the 20 percent who were born Quirkless?

Middle school student Izuku Midoriya wants to be a hero more than anything, but he hasn't got an ounce of power in him. With no chance of ever getting into the prestigious U.A. High School for budding heroes, his life is looking more and more like a dead end. Then an encounter with All Might, the greatest hero of them all gives him a chance to change his destiny…

(Source: Viz Media)""",
                genre = "Action, Adventure, Comedy",
                status = Status.Completed,
            ),
            authorLabel = "Animation studio",
            artistLabel = "Fansub",
            onEditTitle = { },
            onEditAuthor = { },
            onEditArtist = { },
            onEditDescription = { },
            onEditGenre = { },
            onEditStatus = { },
        )
    }
}
