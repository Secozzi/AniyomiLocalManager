package xyz.secozzi.aniyomilocalmanager.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun DetailsScreenContent(
    title: String,
    generateText: String,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    onGenerate: () -> Unit,
    onCopy: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onSettings() }) {
                        Icon(Icons.Default.Settings, null)
                    }

                    IconButton(onClick = { onSearch() }) {
                        Icon(Icons.Default.Search, null)
                    }
                }
            )
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                modifier = Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        bottom = MaterialTheme.spacing.smaller,
                    )
            ) {
                Button(
                    onClick = { onGenerate() },
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.FileDownload, null)
                        Text(text = generateText)
                    }
                }

                FilledIconButton(
                    onClick = { onCopy() },
                ) {
                    Icon(Icons.Default.ContentCopy, null)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller)
        ) {
            content()
        }
    }
}
