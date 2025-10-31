package xyz.secozzi.aniyomilocalmanager.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import java.util.Locale

@Composable
fun SearchResultItem(
    searchItem: SearchResultItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val type = remember(searchItem.type) {
        searchItem.type?.let { type ->
            type.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
        }
    }

    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.small)
            .clip(
                RoundedCornerShape(MaterialTheme.spacing.small),
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(MaterialTheme.spacing.small),
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(MaterialTheme.spacing.medium),
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Column {
            Row {
                AsyncImage(
                    model = searchItem.coverUrl,
                    placeholder = ColorPainter(Color(0x1F888888)),
                    contentDescription = "",
                    modifier = Modifier
                        .height(96.dp)
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.extraSmall),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                Column {
                    Text(
                        text = searchItem.title,
                        modifier = Modifier.padding(end = MaterialTheme.spacing.large),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (!type.isNullOrBlank()) {
                        SearchResultItemDetails(
                            title = "Type",
                            text = type,
                        )
                    }
                    if (!searchItem.startDate.isNullOrBlank()) {
                        SearchResultItemDetails(
                            title = stringResource(R.string.search_started),
                            text = searchItem.startDate,
                        )
                    }
                    if (!searchItem.status.isNullOrBlank()) {
                        SearchResultItemDetails(
                            title = stringResource(R.string.search_status),
                            text = searchItem.status.replace("_", " ")
                                .lowercase()
                                .replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                },
                        )
                    }
                }
            }

            if (!searchItem.description.isNullOrBlank()) {
                Text(
                    text = searchItem.description,
                    modifier = Modifier
                        .paddingFromBaseline(top = 24.dp)
                        .alpha(0.78f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun SearchResultItemDetails(
    title: String,
    text: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
        Text(
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .alpha(0.78f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
