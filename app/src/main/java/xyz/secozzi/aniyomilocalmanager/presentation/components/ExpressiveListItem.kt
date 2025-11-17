package xyz.secozzi.aniyomilocalmanager.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveListItem(
    itemSize: Int,
    index: Int,
    modifier: Modifier = Modifier,
    color: Color? = null,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val top by animateDpAsState(
        if (isPressed) {
            40.dp
        } else {
            if (itemSize == 1 || index == 0) {
                20.dp
            } else {
                4.dp
            }
        },
        motionScheme.fastSpatialSpec(),
    )
    val bottom by animateDpAsState(
        if (isPressed) {
            40.dp
        } else {
            if (itemSize == 1 || index == itemSize - 1) {
                20.dp
            } else {
                4.dp
            }
        },
        motionScheme.fastSpatialSpec(),
    )

    ListItem(
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = color ?: MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = top,
                    topEnd = top,
                    bottomStart = bottom,
                    bottomEnd = bottom,
                ),
            )
            .clickable(
                onClick = { if (onClick != null) onClick() },
                interactionSource = interactionSource,
                enabled = onClick != null,
            ),
    )
}
