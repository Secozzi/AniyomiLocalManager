package xyz.secozzi.aniyomilocalmanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColoredListItem(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    itemSize: Int,
    index: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    SettingsListItem(
        title = title,
        itemSize = itemSize,
        index = index,
        modifier = modifier,
        supportingContent = { subtitle?.let { Text(it) } },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.extraLarge)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.7f),
                )
            }
        },
        onClick = onClick,
    )
}

@PreviewLightDark
@Composable
fun MainSettingsListItemPreview() {
    PreviewContent {
        ColoredListItem(
            title = "Appearance",
            subtitle = "Theme, tabs",
            icon = Icons.Outlined.Palette,
            itemSize = 2,
            index = 0,
            onClick = {},
        )
    }
}
