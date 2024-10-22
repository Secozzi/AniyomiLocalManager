package xyz.secozzi.aniyomilocalmanager.presentation.compontents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import xyz.secozzi.aniyomilocalmanager.R

@Composable
fun MissingDirectory(
    directoryName: String,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.missing_directory, directoryName),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
