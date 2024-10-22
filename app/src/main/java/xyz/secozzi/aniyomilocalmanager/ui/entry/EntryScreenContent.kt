package xyz.secozzi.aniyomilocalmanager.ui.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.ui.theme.MissingColor
import xyz.secozzi.aniyomilocalmanager.ui.theme.PresentColor
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

@Composable
fun EntryScreenContent(
    path: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val directoryName = path.getDirectoryName()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        directoryName,
                        modifier = Modifier.basicMarquee(),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            content()
        }
    }
}

@Composable
fun SelectItem(
    title: String,
    present: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.small)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .combinedClickable(
                onClick = onClick,
            )
            .padding(MaterialTheme.spacing.medium)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Row {
                Text(
                    text = stringResource(R.string.entry_item_status),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = " ",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text =  if (present) {
                        stringResource(R.string.entry_item_present)
                    } else {
                        stringResource(R.string.entry_item_missing)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (present) PresentColor else MissingColor,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd),
            tint = MaterialTheme.colorScheme.outline,
        )
    }
}
