package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch.comonents

import android.view.MotionEvent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun OutlinedNumericChooser(
    value: Int,
    onChange: (Int) -> Unit,
    min: Int,
    max: Int,
    step: Int,
    modifier: Modifier = Modifier,
    isStart: Boolean? = null,
    isCrossing: Boolean? = null,
    suffix: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
    ) {
        RepeatingIconButton(
            onClick = { onChange(value - step) },
            enabled = enabled,
        ) {
            Icon(Icons.Filled.RemoveCircle, null)
        }
        var valueString by remember { mutableStateOf("$value") }
        LaunchedEffect(value) {
            if (valueString.isBlank() && value == 0) return@LaunchedEffect
            valueString = value.toString()
        }
        OutlinedTextField(
            label = label,
            value = valueString,
            onValueChange = { newValue ->
                if (newValue.isBlank()) {
                    valueString = newValue
                    onChange(0)
                }
                runCatching {
                    val intValue = if (newValue.trimStart() == "-") -0 else newValue.toInt()
                    onChange(intValue)
                    valueString = newValue
                }
            },
            isError = value > max || value < min || isCrossing == true,
            supportingText = {
                if (isStart == true) {
                    if (isCrossing == true) {
                        Text(text = stringResource(R.string.episode_start_above_error))
                    } else if (value < min) {
                        Text(text = stringResource(R.string.episode_start_small, min))
                    } else if (value > max) {
                        Text(text = stringResource(R.string.episode_start_big, max))
                    }
                } else {
                    if (isCrossing == true) {
                        Text(text = stringResource(R.string.episode_end_below_error))
                    } else if (value < min) {
                        Text(text = stringResource(R.string.episode_end_small, min))
                    } else if (value > max) {
                        Text(text = stringResource(R.string.episode_end_big, max))
                    }
                }
            },
            suffix = suffix,
            modifier = Modifier.weight(1f)
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = enabled,
        )
        RepeatingIconButton(
            onClick = { onChange(value + step) },
            enabled = enabled,
        ) {
            Icon(Icons.Filled.AddCircle, null)
        }
    }
}

@Composable
fun RepeatingIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    maxDelayMillis: Long = 750,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = .25f,
    content: @Composable () -> Unit,
) {
    val currentClickListener by rememberUpdatedState(onClick)
    var pressed by remember { mutableStateOf(false) }

    IconButton(
        modifier = modifier.pointerInteropFilter {
            pressed = when (it.action) {
                MotionEvent.ACTION_DOWN -> true

                else -> false
            }

            true
        },
        onClick = {},
        enabled = enabled,
        interactionSource = interactionSource,
        content = content,
    )

    LaunchedEffect(pressed, enabled) {
        var currentDelayMillis = maxDelayMillis

        while (enabled && pressed) {
            currentClickListener()
            delay(currentDelayMillis)
            currentDelayMillis =
                (currentDelayMillis - (currentDelayMillis * delayDecayFactor))
                    .toLong().coerceAtLeast(minDelayMillis)
        }
    }
}
