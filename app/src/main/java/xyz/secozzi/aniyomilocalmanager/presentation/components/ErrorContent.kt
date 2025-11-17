package xyz.secozzi.aniyomilocalmanager.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
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
    LaunchedEffect(throwable) {
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

@Composable
fun LogsContainer(
    logs: String,
    modifier: Modifier = Modifier,
) {
    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        SelectionContainer(
            modifier = Modifier.horizontalScroll(scrollStateHorizontal)
                .verticalScroll(scrollStateVertical),
        ) {
            Text(
                text = logs,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp),
            )
        }
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
