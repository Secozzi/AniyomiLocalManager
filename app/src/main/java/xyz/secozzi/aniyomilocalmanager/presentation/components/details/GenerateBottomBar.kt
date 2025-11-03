package xyz.secozzi.aniyomilocalmanager.presentation.components.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenerateBottomBar(
    label: String,
    onGenerate: () -> Unit,
    onCopy: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
            .windowInsetsPadding(NavigationBarDefaults.windowInsets),
    ) {
        Button(
            onClick = onGenerate,
            modifier = Modifier.heightIn(min = MaterialTheme.spacing.extraLarge),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().toolbarContainerColor,
                contentColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().toolbarContentColor,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
            )
            Spacer(Modifier.width(MaterialTheme.spacing.smaller))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(Modifier.width(MaterialTheme.spacing.extraSmall))

        IconButton(
            onClick = onCopy,
            shape = FloatingActionButtonDefaults.shape,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContainerColor,
                contentColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContentColor,
            ),
            modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun GenerateBottomBarPreview() {
    PreviewContent {
        GenerateBottomBar(
            label = "Generate details.json",
            onGenerate = { },
            onCopy = { },
        )
    }
}
