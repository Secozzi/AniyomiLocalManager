package xyz.secozzi.aniyomilocalmanager.presentation.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DownloadingBottomBar() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(bottom = MaterialTheme.spacing.smaller),
        ) {
            CircularWavyProgressIndicator(
                trackColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(MaterialTheme.spacing.larger),
            )
            Spacer(Modifier.width(MaterialTheme.spacing.smaller))
            Text(
                text = stringResource(R.string.cover_downloading_cover),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun DownloadBottomBar(
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .padding(bottom = MaterialTheme.spacing.smaller),
        ) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null,
                modifier = Modifier.size(MaterialTheme.spacing.larger),
            )
            Spacer(Modifier.width(MaterialTheme.spacing.smaller))
            Text(
                text = stringResource(R.string.cover_download_cover),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
