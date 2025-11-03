package xyz.secozzi.aniyomilocalmanager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.ui.theme.DisabledAlpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun <T> SimpleDropdown(
    label: String,
    selected: T?,
    items: List<T>,
    onSelected: (T) -> Unit,
    getDisplayName: @Composable (T) -> String,
    getExtraData: @Composable (T) -> String?,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = selected?.let { getDisplayName(it) } ?: "",
            onValueChange = {},
            label = { Text(text = label) },
            suffix = {
                if (selected != null) {
                    getExtraData(selected)?.let {
                        Text(
                            text = it,
                            modifier = Modifier.alpha(DisabledAlpha),
                        )
                    }
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller)) {
                            Text(text = getDisplayName(item))
                            getExtraData(item)?.let {
                                Text(
                                    text = it,
                                    modifier = Modifier.alpha(DisabledAlpha),
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun SimpleDropdownPreview() {
    PreviewContent {
        SimpleDropdown(
            label = "Menu",
            selected = "1",
            items = listOf("1", "2", "3", "4", "5"),
            onSelected = { },
            getDisplayName = { it },
            getExtraData = { null },
        )
    }
}
