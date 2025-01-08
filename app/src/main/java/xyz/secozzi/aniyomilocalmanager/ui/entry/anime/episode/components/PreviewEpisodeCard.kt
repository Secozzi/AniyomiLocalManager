package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.utils.Constants.disabledAlpha

@Composable
fun PreviewEpisodeCard(
    title: String,
    episodeNumber: Int,
    originalEpisodeNumber: Int? = null,
    extraInfo: List<String>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row {
                    Text(
                        text = buildList {
                            if (episodeNumber != 0) {
                                add(stringResource(R.string.episode_episode, episodeNumber))
                            }
                            addAll(extraInfo)
                        }
                            .filter { it.isNotEmpty() }
                            .joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            originalEpisodeNumber?.let {
                Text(
                    text = "($it)",
                    modifier = Modifier.alpha(disabledAlpha),
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewEpisodeCardPreview() {
    PreviewEpisodeCard(
        title = "Ep. 28 It Would Be Embarrassing When We Met Again",
        episodeNumber = 28,
        originalEpisodeNumber = 28,
        extraInfo = listOf("Episode 28", "2024-03-22"),
        modifier = Modifier,
    )
}
