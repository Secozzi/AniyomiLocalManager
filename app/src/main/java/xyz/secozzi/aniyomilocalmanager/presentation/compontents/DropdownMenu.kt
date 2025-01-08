package xyz.secozzi.aniyomilocalmanager.presentation.compontents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.Constants.disabledAlpha

interface DropdownItem {
    val displayName: String
    val id: Int
    val extraData: Int?
}

@Composable
fun <T : DropdownItem> SimpleDropdown(
    label: String,
    selectedItem: T?,
    items: List<T>,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            readOnly = true,
            value = selectedItem?.displayName ?: "",
            onValueChange = {},
            label = { Text(text = label) },
            suffix = {
                if (selectedItem?.extraData != null) {
                    Text(
                        text = "(${selectedItem.extraData!!})",
                        modifier = Modifier.alpha(disabledAlpha),
                    )
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                        ) {
                            Text(text = item.displayName)
                            if (item.extraData != null) {
                                Text(
                                    text = "(${item.extraData!!})",
                                    modifier = Modifier.alpha(disabledAlpha),
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
