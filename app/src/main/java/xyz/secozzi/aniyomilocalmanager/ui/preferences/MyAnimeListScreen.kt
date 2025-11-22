package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.MyAnimeListPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ButtonGroup
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ButtonGroupEntry
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.presentation.settings.TextFieldDialog
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data object MyAnimeListPreferencesRoute : NavKey

@Composable
fun MyAnimeListPreferencesScreen() {
    val backStack = LocalBackStack.current
    val preferences = koinInject<MyAnimeListPreferences>()

    val prefLang by preferences.prefLang.collectAsState()
    val nameFormat by preferences.nameFormat.collectAsState()
    val scanlatorFormat by preferences.scanlatorFormat.collectAsState()
    val summaryFormat by preferences.summaryFormat.collectAsState()

    var dialog by remember { mutableStateOf<MyAnimeListDialog?>(null) }

    MyAnimeListPreferencesScreenContent(
        onBack = { backStack.removeLastOrNull() },
        prefLang = prefLang,
        onPrefLangClicked = preferences.prefLang::set,
        nameFormat = nameFormat,
        onNameFormatClicked = { dialog = MyAnimeListDialog.NameFormat },
        scanlatorFormat = scanlatorFormat,
        onScanlatorFormatClicked = { dialog = MyAnimeListDialog.ScanlatorFormat },
        summaryFormat = summaryFormat,
        onSummaryFormatClicked = { dialog = MyAnimeListDialog.SummaryFormat },
    )

    when (dialog) {
        MyAnimeListDialog.NameFormat -> {
            TextFieldDialog(
                title = stringResource(R.string.pref_name_format),
                summary = stringResource(R.string.pref_mal_replacements),
                textValue = nameFormat,
                onConfirm = {
                    preferences.nameFormat.set(it)
                    dialog = null
                },
                onDismiss = { dialog = null },
            )
        }
        MyAnimeListDialog.ScanlatorFormat -> {
            TextFieldDialog(
                title = stringResource(R.string.pref_scanlator_format),
                summary = stringResource(R.string.pref_mal_replacements),
                textValue = scanlatorFormat,
                onConfirm = {
                    preferences.scanlatorFormat.set(it)
                    dialog = null
                },
                onDismiss = { dialog = null },
            )
        }
        MyAnimeListDialog.SummaryFormat -> {
            TextFieldDialog(
                title = stringResource(R.string.pref_summary_format),
                summary = stringResource(R.string.pref_mal_replacements),
                textValue = summaryFormat,
                onConfirm = {
                    preferences.summaryFormat.set(it)
                    dialog = null
                },
                onDismiss = { dialog = null },
            )
        }
        null -> {}
    }
}

private sealed interface MyAnimeListDialog {
    data object NameFormat : MyAnimeListDialog
    data object ScanlatorFormat : MyAnimeListDialog
    data object SummaryFormat : MyAnimeListDialog
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MyAnimeListPreferencesScreenContent(
    onBack: () -> Unit,
    prefLang: LangPrefEnum,
    onPrefLangClicked: (LangPrefEnum) -> Unit,
    nameFormat: String,
    onNameFormatClicked: () -> Unit,
    scanlatorFormat: String,
    onScanlatorFormatClicked: () -> Unit,
    summaryFormat: String,
    onSummaryFormatClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_mal_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            // Details
            item {
                Text(
                    text = stringResource(R.string.details_title),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.smaller),
                )
            }
            item {
                val themeEntries = remember {
                    LangPrefEnum.entries.map { ButtonGroupEntry(it, it.titleRes) }.toPersistentList()
                }
                val selected = remember(prefLang) {
                    ButtonGroupEntry(prefLang, prefLang.titleRes)
                }

                SettingsListItem(
                    title = stringResource(R.string.pref_title_lang),
                    itemSize = 1,
                    index = 0,
                    supportingContent = {
                        ButtonGroup(
                            entries = themeEntries,
                            selected = selected,
                            onSelect = onPrefLangClicked,
                        )
                    },
                )
            }

            item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

            // Episodes
            item {
                Text(
                    text = stringResource(R.string.label_episodes),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.smaller),
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_name_format),
                    icon = Icons.AutoMirrored.Filled.List,
                    itemSize = 3,
                    index = 0,
                    supportingContent = { Text(nameFormat) },
                    onClick = onNameFormatClicked,
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_scanlator_format),
                    icon = Icons.Default.Brush,
                    itemSize = 3,
                    index = 1,
                    supportingContent = { Text(scanlatorFormat) },
                    onClick = onScanlatorFormatClicked,
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_summary_format),
                    icon = Icons.Default.Description,
                    itemSize = 3,
                    index = 2,
                    supportingContent = { Text(summaryFormat) },
                    onClick = onSummaryFormatClicked,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun MyAnimeListPreferencesScreenContentPreview() {
    PreviewContent {
        MyAnimeListPreferencesScreenContent(
            onBack = { },
            prefLang = LangPrefEnum.English,
            onPrefLangClicked = { },
            nameFormat = "Ep. %ep - %eng",
            onNameFormatClicked = { },
            scanlatorFormat = "",
            onScanlatorFormatClicked = { },
            summaryFormat = "",
            onSummaryFormatClicked = { },
        )
    }
}
