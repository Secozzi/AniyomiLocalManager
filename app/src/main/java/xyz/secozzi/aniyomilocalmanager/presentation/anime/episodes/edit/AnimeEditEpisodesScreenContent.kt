package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.edit

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.edit.components.DateTextField
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.presentation.components.FloatingBottomBar
import xyz.secozzi.aniyomilocalmanager.presentation.components.edit.EditEntryHeader
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsSwitch
import xyz.secozzi.aniyomilocalmanager.presentation.utils.plus
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit.AnimeEditEpisodeScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit.AnimeEditEpisodeScreenViewModel.ValidEntry
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun AnimeEditEpisodesScreenContent(
    state: AnimeEditEpisodeScreenViewModel.State,
    name: String,
    lazyListState: LazyListState,
    validIndexes: ImmutableList<ValidEntry>,
    expandedState: ImmutableList<Boolean>,
    onExpandedStateChange: (Int) -> Unit,
    onEditNumber: (Int, String) -> Unit,
    onEditTitle: (Int, String) -> Unit,
    onEditFiller: (Int, Boolean) -> Unit,
    onEditDescription: (Int, String) -> Unit,
    onEditScanlator: (Int, String) -> Unit,
    onEditPreviewUrl: (Int, String) -> Unit,
    onEditDate: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onSave: () -> Unit,
    onAdd: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
            )
        },
        bottomBar = {
            FloatingBottomBar(
                label = stringResource(R.string.episode_edit_save_details),
                enabled = remember(validIndexes) {
                    validIndexes.all { it.isValid() }
                },
                primaryIcon = Icons.Filled.Save,
                secondaryIcon = Icons.Default.Add,
                onGenerate = onSave,
                onSecondary = onAdd,
            )
        },
    ) { contentPadding ->
        when (state) {
            AnimeEditEpisodeScreenViewModel.State.Idle -> { }
            is AnimeEditEpisodeScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.throwable,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeEditEpisodeScreenViewModel.State.Success -> {
                SuccessContent(
                    state = state,
                    lazyListState = lazyListState,
                    contentPadding = contentPadding,
                    expandedState = expandedState,
                    onExpandedStateChange = onExpandedStateChange,
                    validIndexes = validIndexes,
                    onEditNumber = onEditNumber,
                    onEditTitle = onEditTitle,
                    onEditFiller = onEditFiller,
                    onEditDescription = onEditDescription,
                    onEditScanlator = onEditScanlator,
                    onEditPreviewUrl = onEditPreviewUrl,
                    onEditDate = onEditDate,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SuccessContent(
    state: AnimeEditEpisodeScreenViewModel.State.Success,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
    expandedState: ImmutableList<Boolean>,
    onExpandedStateChange: (Int) -> Unit,
    validIndexes: ImmutableList<ValidEntry>,
    onEditNumber: (Int, String) -> Unit,
    onEditTitle: (Int, String) -> Unit,
    onEditFiller: (Int, Boolean) -> Unit,
    onEditDescription: (Int, String) -> Unit,
    onEditScanlator: (Int, String) -> Unit,
    onEditPreviewUrl: (Int, String) -> Unit,
    onEditDate: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = contentPadding + PaddingValues(bottom = MaterialTheme.spacing.smaller),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        itemsIndexed(state.data) { i, ep ->
            val interactionSource = remember { MutableInteractionSource() }
            val expanded = expandedState[i]
            val valid = validIndexes.getOrNull(i)
            val isDuplicate = valid?.isDuplicate ?: false
            val isValidDate = valid?.isValidDate ?: true

            ExpressiveListItem(
                itemSize = state.data.size,
                index = i,
                modifier = Modifier
                    .animateContentSize()
                    .indication(
                        interactionSource = interactionSource,
                        indication = ripple(),
                    ),
                headlineContent = {
                    EditEntryHeader(
                        isInvalid = valid?.isValid() == false,
                        number = ep.episodeNumber,
                        name = ep.name ?: "",
                        expanded = expanded,
                        onClick = { onExpandedStateChange(i) },
                        interactionSource = interactionSource,
                    )
                },
                supportingContent = {
                    if (expanded) {
                        Column(
                            modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        ) {
                            OutlinedTextField(
                                value = ep.name ?: "",
                                onValueChange = { onEditTitle(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_title)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                                OutlinedTextField(
                                    value = ep.episodeNumber.toString(),
                                    onValueChange = { onEditNumber(i, it) },
                                    label = { Text(text = stringResource(R.string.episode_edit_episode_number)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    isError = isDuplicate,
                                    supportingText = {
                                        if (isDuplicate) {
                                            Text(text = stringResource(R.string.episode_edit_invalid_episode_number))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    SettingsSwitch(
                                        checked = ep.fillermark,
                                        onClick = { onEditFiller(i, it) },
                                    )
                                    Text(
                                        text = stringResource(R.string.episode_edit_filler),
                                        style = MaterialTheme.typography.bodyLargeEmphasized,
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = ep.summary ?: "",
                                onValueChange = { onEditDescription(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_description)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = ep.scanlator ?: "",
                                onValueChange = { onEditScanlator(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_scanlator)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = ep.previewUrl ?: "",
                                onValueChange = { onEditPreviewUrl(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_preview_url)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            DateTextField(
                                label = stringResource(R.string.episode_edit_date),
                                text = ep.dateUpload ?: "1970-01-01T00:00:00",
                                isError = !isValidDate,
                                errorMessage = stringResource(R.string.episode_edit_invalid_date),
                                onTextChange = { onEditDate(i, it) },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(
                                        end = MaterialTheme.spacing.smaller,
                                    )
                                    .offset(y = -MaterialTheme.spacing.smaller)
                                    .size(MaterialTheme.spacing.larger)
                                    .combinedClickable(
                                        onClick = { onDelete(i) },
                                    )
                                    .align(Alignment.End),
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun AnimeEditEpisodesScreenContentPreview() {
    PreviewContent {
        AnimeEditEpisodesScreenContent(
            state = AnimeEditEpisodeScreenViewModel.State.Success(
                persistentListOf(
                    EpisodeDetails(
                        episodeNumber = 1f,
                        name = "Ep. 1 - What? Moon over the Ruined Castle?",
                        dateUpload = "2024-01-08T00:00:00",
                        fillermark = false,
                        scanlator = null,
                        summary = """Mikoto and Shiki are making their way to Rotsgard Academy in hopes of opening a new store. Meanwhile, back in the demiplane, everyone is helping Mio practice her cooking skills by taste testing her dishes.

Source: crunchyroll""",
                        previewUrl = null,
                    ),
                    EpisodeDetails(
                        episodeNumber = 2f,
                        name = "Ep. 2 - The Heroes Are a Couple of Beauties",
                        dateUpload = "2024-01-15T00:00:00",
                        fillermark = false,
                        scanlator = null,
                        summary = """We meet one of Misumi Makoto's classmates back on earth, who also has a curious encounter with a certain goddess who presides over a certain isekai. She and one other are sent there as heroes to help the hyumans defeat the demons.

Source: Crunchyroll""",
                        previewUrl = null,
                    ),
                ),
            ),
            lazyListState = rememberLazyListState(),
            name = "Tsukimichi",
            validIndexes = persistentListOf(
                ValidEntry(
                    isDuplicate = false,
                    isValidDate = true,
                ),
                ValidEntry(
                    isDuplicate = false,
                    isValidDate = true,
                ),
            ),
            expandedState = persistentListOf(false, true),
            onExpandedStateChange = { },
            onEditNumber = { _, _ -> },
            onEditTitle = { _, _ -> },
            onEditFiller = { _, _ -> },
            onEditDescription = { _, _ -> },
            onEditScanlator = { _, _ -> },
            onEditPreviewUrl = { _, _ -> },
            onEditDate = { _, _ -> },
            onDelete = { },
            onSave = { },
            onAdd = { },
            onBack = { },
        )
    }
}
