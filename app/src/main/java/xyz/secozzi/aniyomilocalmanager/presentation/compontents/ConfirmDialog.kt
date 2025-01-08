package xyz.secozzi.aniyomilocalmanager.presentation.compontents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun ConfirmDialog(
    title: String,
    subtitle: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onCancel,
        modifier = modifier,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )
                Text(
                    subtitle,
                    color = AlertDialogDefaults.textContentColor,
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onCancel) {
                        Text(stringResource(R.string.generic_cancel))
                    }
                    TextButton(onConfirm) {
                        Text(stringResource(R.string.generic_confirm))
                    }
                }
            }
        }
    }
}
