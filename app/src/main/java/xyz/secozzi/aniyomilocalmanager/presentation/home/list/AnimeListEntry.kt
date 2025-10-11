package xyz.secozzi.aniyomilocalmanager.presentation.home.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.home.AnimeListEntry
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun AnimeListEntry(
    itemSize: Int,
    index: Int,
    entry: AnimeListEntry,
    onClick: () -> Unit,
) {
    val resources = LocalResources.current

    ExpressiveListItem(
        itemSize = itemSize,
        index = index,
        headlineContent = {
            Text(
                text = entry.name,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            Icon(
                imageVector = if (entry.isSeason) {
                    ImageVector.vectorResource(R.drawable.tv_guide_24px)
                } else {
                    Icons.Outlined.Movie
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
        },
        supportingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
            ) {
                Text(
                    text = resources.getQuantityString(R.plurals.entry_item_count, entry.size, entry.size),
                    style = MaterialTheme.typography.labelSmall,
                )

                if (entry.hasCover) {
                    Icon(
                        imageVector = Icons.Filled.Image,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }

                if (entry.hasDetails) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.quick_reference_24px),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }

                if (entry.hasEpisodes) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = entry.lastModified,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        onClick = onClick,
    )
}

@PreviewLightDark
@Composable
private fun AnimeListEntryPreview() {
    val entry = AnimeListEntry(
        isSeason = false,
        name = "Astra.Lost.in.Space.S01.1080p.BluRay.Opus2.0.H.264-LYS1TH3A",
        lastModified = "29/10/2024 - 10:07:22",
        size = 5,
        hasCover = true,
        hasDetails = false,
        hasEpisodes = true,
    )

    PreviewContent {
        AnimeListEntry(2, 1, entry, {})
    }
}
