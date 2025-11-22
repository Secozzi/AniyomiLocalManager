package xyz.secozzi.aniyomilocalmanager.presentation.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingBottomBar(
    label: String,
    enabled: Boolean = true,
    primaryIcon: ImageVector = Icons.Filled.Download,
    secondaryIcon: ImageVector = Icons.Default.ContentCopy,
    onGenerate: () -> Unit,
    onSecondary: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
            .windowInsetsPadding(NavigationBarDefaults.windowInsets),
    ) {
        Button(
            onClick = onGenerate,
            enabled = enabled,
            modifier = Modifier.heightIn(min = MaterialTheme.spacing.extraLarge),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().toolbarContainerColor,
                contentColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().toolbarContentColor,
            ),
        ) {
            Icon(
                imageVector = primaryIcon,
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
            onClick = onSecondary,
            enabled = enabled,
            shape = FloatingActionButtonDefaults.shape,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContainerColor,
                contentColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContentColor,
                disabledContainerColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContainerColor.copy(
                    alpha = DisabledAlpha,
                ),
                disabledContentColor = FloatingToolbarDefaults.vibrantFloatingToolbarColors().fabContentColor.copy(
                    alpha = DisabledAlpha,
                ),
            ),
            modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
        ) {
            Icon(
                imageVector = secondaryIcon,
                contentDescription = null,
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun GenerateBottomBarPreview() {
    PreviewContent {
        FloatingBottomBar(
            label = "Generate details.json",
            enabled = false,
            onGenerate = { },
            onSecondary = { },
        )
    }
}
