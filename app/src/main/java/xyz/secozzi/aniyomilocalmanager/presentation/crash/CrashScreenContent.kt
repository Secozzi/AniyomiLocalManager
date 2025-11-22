package xyz.secozzi.aniyomilocalmanager.presentation.crash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.InfoIcon
import xyz.secozzi.aniyomilocalmanager.presentation.components.LogsContainer
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashScreenContent(
    exceptionString: String,
    logcat: String,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onRestart: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier.navigationBarsPadding().fillMaxWidth(),
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    modifier = Modifier.align(Alignment.Center),
                    floatingActionButton = {
                        FloatingToolbarDefaults.VibrantFloatingActionButton(
                            onClick = onRestart,
                        ) {
                            Icon(Icons.Default.RestartAlt, null)
                        }
                    },
                ) {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, null)
                    }

                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, null)
                    }
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    InfoIcon(Icons.Outlined.BugReport)

                    Text(
                        stringResource(R.string.crash_screen_title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }

            item {
                Text(
                    stringResource(R.string.crash_screen_subtitle, stringResource(R.string.app_name)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Text(
                    stringResource(R.string.crash_screen_logs_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            item {
                LogsContainer(exceptionString)
            }

            item {
                Text(
                    text = stringResource(R.string.crash_screen_logcat_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            item {
                LogsContainer(logcat)
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun CrashScreenContentPreview() {
    PreviewContent {
        CrashScreenContent(
            exceptionString = """Exception:
java.lang.ArithmeticException: divide by zero""",
            logcat = """--------- beginning of main
11-22 16:42:05.281 13785 13785 I almanager.debug: Late-enabling -Xcheck:jni
11-22 16:42:05.301 13785 13785 I almanager.debug: Using CollectorTypeCMC GC.""",
            onShare = { },
            onCopy = { },
            onRestart = { },
        )
    }
}
