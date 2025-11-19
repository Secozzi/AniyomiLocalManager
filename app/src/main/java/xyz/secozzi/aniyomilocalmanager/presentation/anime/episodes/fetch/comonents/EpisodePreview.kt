package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.comonents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun EpisodePreview(
    itemSize: Int,
    index: Int,
    episodeDetails: EpisodeDetails,
    extraData: String? = null,
    color: Color? = null,
) {
    ExpressiveListItem(
        itemSize = itemSize,
        index = index,
        color = color,
        headlineContent = {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = episodeDetails.name ?: "",
                    modifier = Modifier.weight(1f),
                )

                if (extraData != null) {
                    Text(
                        text = extraData,
                        modifier = Modifier.alpha(DisabledAlpha),
                        softWrap = false,
                        maxLines = 1,
                    )
                }
            }
        },
        supportingContent = {
            Column {
                if (episodeDetails.summary?.isNotEmpty() == true) {
                    Text(episodeDetails.summary)
                }

                Text(
                    text = buildList(3) {
                        if (episodeDetails.episodeNumber != 0f) {
                            add(stringResource(R.string.episode_episode, episodeDetails.episodeNumber))
                        }

                        if (episodeDetails.dateUpload != null) {
                            add(episodeDetails.dateUpload)
                        }

                        if (episodeDetails.scanlator != null) {
                            add(episodeDetails.scanlator)
                        }
                    }
                        .filter { it.isNotEmpty() }
                        .joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
    )
}

@Composable
@PreviewLightDark
private fun EpisodePreviewPreview() {
    PreviewContent {
        EpisodePreview(
            itemSize = 2,
            index = 0,
            episodeDetails = EpisodeDetails(
                episodeNumber = 1f,
                name = "Ep. 1 - What? Moon over the Ruined Castle?",
                dateUpload = "2024-01-08",
                fillermark = false,
                scanlator = null,
                summary = """Mikoto and Shiki are making their way to Rotsgard Academy in hopes of opening a new store. Meanwhile, back in the demiplane, everyone is helping Mio practice her cooking skills by taste testing her dishes.

Source: crunchyroll""",
                previewUrl = null,
            ),
            extraData = "(23)",
            color = null,
        )
    }
}
