package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.comonents.EpisodePreview
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.comonents.OutlinedNumericChooser
import xyz.secozzi.aniyomilocalmanager.presentation.components.DropdownItem
import xyz.secozzi.aniyomilocalmanager.presentation.components.SimpleDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.utils.toDisplayString
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EpisodeFetchDetails(
    episodesMap: ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>,
    videoCount: Int?,
    selectedType: EpisodeType?,
    onSelectedTypeChange: (EpisodeType) -> Unit,
    start: Int,
    onStartChange: (Int) -> Unit,
    end: Int,
    onEndChange: (Int) -> Unit,
    offset: Int,
    onOffsetChange: (Int) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val localDensity = LocalDensity.current
    var textHeightDp by remember { mutableStateOf(0.dp) }

    val dropdownItems = episodesMap.entries.map { (type, ep) ->
        type.toDropdownItem(ep.size)
    }.toPersistentList()
    val selected = remember(selectedType) {
        dropdownItems.firstOrNull { it.item == selectedType }
    }
    val currentEntry = remember(selectedType) {
        episodesMap.entries.firstOrNull { it.key == selectedType }
    }

    Column(
        modifier = modifier
            .padding(
                start = MaterialTheme.spacing.small,
                end = MaterialTheme.spacing.small,
                top = MaterialTheme.spacing.small,
            ),
    ) {
        SimpleDropdown(
            label = stringResource(R.string.episode_type_label),
            selected = selected,
            items = dropdownItems,
            onSelected = onSelectedTypeChange,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedNumericChooser(
            value = start,
            onChange = onStartChange,
            min = 1,
            max = currentEntry?.value?.size ?: 1,
            step = 1,
            label = { Text(text = stringResource(R.string.episode_start_label)) },
            isStart = true,
            isCrossing = start > end,
        )

        OutlinedNumericChooser(
            value = end,
            onChange = onEndChange,
            min = 1,
            max = currentEntry?.value?.size ?: 1,
            step = 1,
            label = { Text(text = stringResource(R.string.episode_end_label)) },
            isStart = false,
            isCrossing = start > end,
        )

        OutlinedNumericChooser(
            value = offset,
            onChange = onOffsetChange,
            min = Int.MIN_VALUE,
            max = Int.MAX_VALUE,
            step = 1,
            label = { Text(text = stringResource(R.string.episode_offset_label)) },
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        Row {
            Icon(
                imageVector = Icons.Outlined.Numbers,
                contentDescription = null,
                modifier = Modifier.padding(start = 14.dp),
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.smaller))

            Text(
                text = stringResource(R.string.episode_video_count),
                modifier = Modifier.onGloballyPositioned {
                    textHeightDp = with(localDensity) { it.size.height.toDp() }
                },
            )

            Spacer(modifier = Modifier.weight(1.0f))

            if (videoCount == null) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier.padding(end = 12.dp).size(textHeightDp),
                )
            } else {
                Text(
                    text = videoCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        LazyColumn(
            contentPadding = PaddingValues(bottom = bottomPadding + MaterialTheme.spacing.smaller),
        ) {
            if (currentEntry != null && currentEntry.value.isNotEmpty()) {
                val startEpisode = currentEntry.value.getOrNull(start - 1)
                val endEpisode = currentEntry.value.getOrNull(end - 1)

                if (startEpisode != null) {
                    item {
                        EpisodePreview(
                            itemSize = if (endEpisode == null) 1 else 2,
                            index = 0,
                            episodeDetails = startEpisode.copy(episodeNumber = startEpisode.episodeNumber + offset),
                            extraData = "(${startEpisode.episodeNumber.toDisplayString()})",
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                if (endEpisode != null) {
                    item {
                        EpisodePreview(
                            itemSize = 2,
                            index = 1,
                            episodeDetails = endEpisode.copy(episodeNumber = endEpisode.episodeNumber + offset),
                            extraData = "(${endEpisode.episodeNumber.toDisplayString()})",
                        )
                    }
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun EpisodeType.toDropdownItem(itemSize: Int): DropdownItem<EpisodeType> {
    return DropdownItem(
        item = this,
        displayName = stringResource(this.stringRes),
        extraData = "($itemSize)",
    )
}
