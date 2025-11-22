package xyz.secozzi.aniyomilocalmanager.presentation.manga.chapters

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.manga.model.ComicInfo
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.presentation.components.LoadingContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.edit.EditEntryHeader
import xyz.secozzi.aniyomilocalmanager.ui.manga.chapters.MangaChaptersScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun MangaChaptersScreenContent(
    state: MangaChaptersScreenViewModel.State,
    name: String,
    unsaved: ImmutableSet<Int>,
    loading: ImmutableSet<Int>,
    onBack: () -> Unit,
    onEditTitle: (Int, String) -> Unit,
    onEditNumber: (Int, String) -> Unit,
    onEditScanlator: (Int, String) -> Unit,
    onSave: (Int) -> Unit,
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
    ) { contentPadding ->
        when (state) {
            MangaChaptersScreenViewModel.State.Idle -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaChaptersScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.throwable,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is MangaChaptersScreenViewModel.State.Success -> {
                SuccessContent(
                    state = state,
                    contentPadding = contentPadding,
                    unsaved = unsaved,
                    loading = loading,
                    onEditTitle = onEditTitle,
                    onEditNumber = onEditNumber,
                    onEditScanlator = onEditScanlator,
                    onSave = onSave,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    state: MangaChaptersScreenViewModel.State.Success,
    contentPadding: PaddingValues,
    unsaved: ImmutableSet<Int>,
    loading: ImmutableSet<Int>,
    onEditTitle: (Int, String) -> Unit,
    onEditNumber: (Int, String) -> Unit,
    onEditScanlator: (Int, String) -> Unit,
    onSave: (Int) -> Unit,
) {
    val expandedState = remember(state.data.size) {
        List(state.data.size) { false }.toMutableStateList()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = contentPadding,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        itemsIndexed(state.data) { i, entry ->
            val ch = entry.data
            val interactionSource = remember { MutableInteractionSource() }
            val expanded = expandedState[i]
            val unsaved = i in unsaved
            val loading = i in loading

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
                        isInvalid = unsaved,
                        invalidIcon = Icons.Default.Edit,
                        number = ch.number?.value?.toFloatOrNull() ?: 1f,
                        name = ch.title?.value ?: "",
                        expanded = expanded,
                        onClick = { expandedState[i] = !expanded },
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
                                value = ch.title?.value ?: "",
                                onValueChange = { onEditTitle(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_title)) },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            OutlinedTextField(
                                value = ch.number?.value ?: "0.0",
                                onValueChange = { onEditNumber(i, it) },
                                label = { Text(text = stringResource(R.string.chapter_edit_chapter_number)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                            )

                            OutlinedTextField(
                                value = ch.translator?.value ?: "",
                                onValueChange = { onEditScanlator(i, it) },
                                label = { Text(text = stringResource(R.string.episode_edit_scanlator)) },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.fillMaxWidth().weight(1f))

                                if (loading) {
                                    CircularProgressIndicator(
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(
                                            end = MaterialTheme.spacing.smaller,
                                        )
                                        .size(MaterialTheme.spacing.larger)
                                        .combinedClickable(
                                            onClick = { onSave(i) },
                                            enabled = unsaved,
                                        )
                                        .alpha(if (unsaved) 1f else DisabledAlpha),
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun MangaChaptersScreenContentPreview() {
    PreviewContent {
        val state = MangaChaptersScreenViewModel.State.Success(
            data = listOf(
                MangaChaptersScreenViewModel.Entry(
                    data = ComicInfo(
                        title = ComicInfo.Title("Chapter 1"),
                        number = ComicInfo.Number("1"),
                        translator = ComicInfo.Translator("Webtoon"),
                        series = null,
                        summary = null,
                        writer = null,
                        penciller = null,
                        genre = null,
                        publishingStatus = null,
                    ),
                    path = "",
                    isDirectory = true,
                    isNewComicInfo = false,
                ),
                MangaChaptersScreenViewModel.Entry(
                    data = ComicInfo(
                        title = ComicInfo.Title("Chapter 2"),
                        number = ComicInfo.Number("2"),
                        translator = null,
                        series = null,
                        summary = null,
                        writer = null,
                        penciller = null,
                        genre = null,
                        publishingStatus = null,
                    ),
                    path = "",
                    isDirectory = true,
                    isNewComicInfo = false,
                ),
            ),
        )

        MangaChaptersScreenContent(
            state = state,
            name = "Tower of God",
            unsaved = persistentSetOf(0),
            loading = persistentSetOf(1),
            onBack = { },
            onEditTitle = { _, _ -> },
            onEditNumber = { _, _ -> },
            onEditScanlator = { _, _ -> },
            onSave = { },
        )
    }
}
