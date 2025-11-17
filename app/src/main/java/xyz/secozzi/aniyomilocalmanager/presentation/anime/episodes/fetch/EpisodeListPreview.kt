package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.comonents.EpisodePreview
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun EpisodeListPreview(
    episodesMap: ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>,
    modifier: Modifier = Modifier,
) {
    val expandedState = remember(episodesMap) { episodesMap.map { false }.toMutableStateList() }

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .padding(
                start = MaterialTheme.spacing.small,
                end = MaterialTheme.spacing.small,
                top = MaterialTheme.spacing.small,
            )
            .verticalScroll(rememberScrollState()),
    ) {
        episodesMap.entries.forEachIndexed { t, (type, episodes) ->
            val expanded = expandedState[t]

            Card(
                type = type,
                episodes = episodes,
                itemSize = episodesMap.size,
                index = t,
                expanded = expanded,
                onExpand = { expandedState[t] = !expanded },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Card(
    type: EpisodeType,
    episodes: ImmutableList<EpisodeDetails>,
    itemSize: Int,
    index: Int,
    expanded: Boolean,
    onExpand: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val top by animateDpAsState(
        if (isPressed) {
            40.dp
        } else {
            if (itemSize == 1 || index == 0) {
                20.dp
            } else {
                4.dp
            }
        },
        motionScheme.fastSpatialSpec(),
    )
    val bottom by animateDpAsState(
        if (isPressed) {
            40.dp
        } else {
            if (itemSize == 1 || index == itemSize - 1) {
                20.dp
            } else {
                4.dp
            }
        },
        motionScheme.fastSpatialSpec(),
    )

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = top,
                    topEnd = top,
                    bottomStart = bottom,
                    bottomEnd = bottom,
                ),
            )
            .indication(
                interactionSource = interactionSource,
                indication = ripple(),
            )
            .animateContentSize()
            .background(color = type.getColor())
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small,
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
        ) {
            CardHeader(
                type = type,
                episodeSize = episodes.size,
                expanded = expanded,
                interactionSource = interactionSource,
                onExpand = onExpand,
            )

            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        itemsIndexed(episodes) { i, ep ->
                            EpisodePreview(
                                itemSize = episodes.size,
                                index = i,
                                episodeDetails = ep,
                                extraData = null,
                                color = type.getColor().copy(alpha = 0.2f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardHeader(
    type: EpisodeType,
    episodeSize: Int,
    expanded: Boolean,
    interactionSource: MutableInteractionSource,
    onExpand: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.combinedClickable(
            onClick = onExpand,
            interactionSource = interactionSource,
            indication = null,
        ),
    ) {
        Text(
            text = stringResource(type.stringRes),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "($episodeSize)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.alpha(DisabledAlpha),
        )

        Spacer(modifier = Modifier.weight(1f))

        if (expanded) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        } else {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }
}

// "500" colors from https://m2.material.io/design/color/the-color-system.html#tools-for-picking-colors
private fun EpisodeType.getColor(): Color {
    return when (this) {
        EpisodeType.Regular -> Color(0x402196F3)
        EpisodeType.Special -> Color(0x404CAF50)
        EpisodeType.Credit -> Color(0x40FFEB3B)
        EpisodeType.Trailer -> Color(0x40F44336)
        EpisodeType.Parody -> Color(0x40E91E63)
        EpisodeType.Other -> Color(0x40673AB7)
    }
}

@Composable
@PreviewLightDark
private fun EpisodePreviewPreview() {
    PreviewContent {
        val episodes = persistentListOf(
            EpisodeDetails(
                episodeNumber = 1,
                name = "Ep. 1 - What? Moon over the Ruined Castle?",
                dateUpload = "2024-01-08",
                fillermark = false,
                scanlator = null,
                summary = """Mikoto and Shiki are making their way to Rotsgard Academy in hopes of opening a new store. Meanwhile, back in the demiplane, everyone is helping Mio practice her cooking skills by taste testing her dishes.

Source: crunchyroll""",
                previewUrl = null,
            ),
            EpisodeDetails(
                episodeNumber = 2,
                name = "Ep. 2 - The Heroes Are a Couple of Beauties",
                dateUpload = "2024-01-15",
                fillermark = false,
                scanlator = null,
                summary = """We meet one of Misumi Makoto's classmates back on earth, who also has a curious encounter with a certain goddess who presides over a certain isekai. She and one other are sent there as heroes to help the hyumans defeat the demons.

Source: Crunchyroll""",
                previewUrl = null,
            ),
        )
        val credit = persistentListOf(
            EpisodeDetails(
                episodeNumber = 1,
                name = "Utopia (1-8, 10-12)",
                dateUpload = "2024-01-08",
                fillermark = false,
                scanlator = null,
                summary = null,
                previewUrl = null,
            ),
            EpisodeDetails(
                episodeNumber = 1,
                name = "Reversal (13-25)",
                dateUpload = "2024-04-01",
                fillermark = false,
                scanlator = null,
                summary = null,
                previewUrl = null,
            ),
        )
        val data = persistentMapOf(
            EpisodeType.Regular to episodes,
            EpisodeType.Credit to credit,
        )
        EpisodeListPreview(data)
    }
}
