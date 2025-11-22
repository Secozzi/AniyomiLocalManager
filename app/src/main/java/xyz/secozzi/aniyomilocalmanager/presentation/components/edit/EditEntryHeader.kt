package xyz.secozzi.aniyomilocalmanager.presentation.components.edit

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import xyz.secozzi.aniyomilocalmanager.presentation.utils.toDisplayString
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun EditEntryHeader(
    isInvalid: Boolean,
    invalidIcon: ImageVector = Icons.Default.ErrorOutline,
    number: Float,
    name: String,
    expanded: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource?,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            ),
    ) {
        if (isInvalid) {
            Icon(
                imageVector = invalidIcon,
                contentDescription = null,
            )
        }

        Text(
            text = "(${number.toDisplayString()})",
            modifier = Modifier.alpha(DisabledAlpha),
        )

        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (expanded) {
            Icon(Icons.Default.KeyboardArrowUp, null)
        } else {
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
    }
}
