package xyz.secozzi.aniyomilocalmanager.presentation.directorylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DirectoryListing(
    data: LocalEntryData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var time: String? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        data.lastModified?.let {
            time = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))
        }
    }

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(
                vertical = MaterialTheme.spacing.small,
                horizontal = MaterialTheme.spacing.medium,
            ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Folder, null)
        Column {
            Text(
                text = data.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = time ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )

                val itemsName = when (data.size) {
                    0 -> "0 items"
                    1 -> "1 item"
                    else -> "${data.size} items"
                }

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))

                Text(
                    text = "• $itemsName",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                val presentColor = MaterialTheme.colorScheme.primary
                val missingColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

                Icon(
                    Icons.Rounded.Image,
                    null,
                    tint = if (data.hasInfo[0]) presentColor else missingColor,
                    modifier = Modifier.size(22.dp),
                )

                Icon(
                    painterResource(R.drawable.ic_quick_reference),
                    null,
                    tint = if (data.hasInfo[1]) presentColor else missingColor,
                    modifier = Modifier.size(22.dp),
                )

                if (data.hasInfo.size == 3) {
                    Icon(
                        painterResource(R.drawable.ic_playlist_play),
                        null,
                        tint = if (data.hasInfo[2]) presentColor else missingColor,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DirectoryListingPreview() {
    val data = LocalEntryData(
        name = "Berserk",
        lastModified = 1728772881000L,
        size = 4,
        hasInfo = listOf(false, true, true),
    )

    DirectoryListing(data, onClick = {})
}
