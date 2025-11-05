package xyz.secozzi.aniyomilocalmanager.presentation.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ExpressiveListItem

@Composable
fun SettingsListItem(
    title: String,
    icon: ImageVector,
    itemSize: Int,
    index: Int,
    modifier: Modifier = Modifier,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    SettingsListItem(
        title = title,
        itemSize = itemSize,
        index = index,
        modifier = modifier,
        supportingContent = supportingContent,
        leadingContent = { Icon(icon, null) },
        trailingContent = trailingContent,
        onClick = onClick,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SettingsListItem(
    title: String,
    itemSize: Int,
    index: Int,
    modifier: Modifier = Modifier,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    ExpressiveListItem(
        itemSize = itemSize,
        index = index,
        modifier = modifier,
        headlineContent = { Text(text = title) },
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        onClick = onClick,
    )
}

@PreviewLightDark
@Composable
private fun SettingsListItemPreview() {
    PreviewContent {
        SettingsListItem(
            title = "Material You",
            itemSize = 2,
            index = 1,
            icon = Icons.Outlined.Contrast,
            supportingContent = {
                Text("Enable Material You dynamic colors.")
            },
            trailingContent = {
                SettingsSwitch(checked = true) { }
            },
            onClick = {},
        )
    }
}
