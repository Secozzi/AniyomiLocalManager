package xyz.secozzi.aniyomilocalmanager.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun ErrorContent(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        Log.e("aniyomi-local-manager", throwable.stackTraceToString())
    }

    Column(
        modifier = modifier.padding(horizontal = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InfoIcon(Icons.Outlined.BugReport)

        Text(text = stringResource(R.string.on_error))
        Text(text = throwable.message ?: "")
        LogsContainer(throwable.stackTraceToString())
    }
}

@PreviewLightDark
@Composable
private fun ErrorContentPreview() {
    PreviewContent {
        try {
            1 / 0
        } catch (e: Exception) {
            ErrorContent(
                e,
                modifier = Modifier.height(500.dp),
            )
        }
    }
}
