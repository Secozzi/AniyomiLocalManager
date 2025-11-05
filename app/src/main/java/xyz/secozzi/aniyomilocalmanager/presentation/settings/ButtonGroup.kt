package xyz.secozzi.aniyomilocalmanager.presentation.settings

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Stable
data class ButtonGroupEntry<T>(
    val item: T,
    @param:StringRes val stringRes: Int,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ButtonGroup(
    entries: ImmutableList<ButtonGroupEntry<T>>,
    selected: ButtonGroupEntry<T>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        entries.forEachIndexed { index, entry ->
            ToggleButton(
                checked = entry == selected,
                onCheckedChange = { onSelect(entry.item) },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = MaterialTheme.spacing.extraSmall)
                    .height(40.dp),
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                AnimatedVisibility(
                    entry == selected,
                    enter = scaleIn(motionScheme.fastSpatialSpec()) +
                        expandHorizontally(motionScheme.fastSpatialSpec()) +
                        fadeIn(),
                    exit = scaleOut(motionScheme.fastSpatialSpec()) +
                        shrinkHorizontally(motionScheme.fastSpatialSpec()) +
                        fadeOut(),
                ) {
                    Icon(Icons.Outlined.Check, null)
                }
                Spacer(Modifier.size(MaterialTheme.spacing.extraSmall))
                Text(
                    text = stringResource(entry.stringRes),
                    color = if (entry == selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
