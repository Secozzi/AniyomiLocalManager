package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.MangaBakaPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ButtonGroup
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data object MangaBakaPreferencesRoute : NavKey

@Composable
fun MangaBakaPreferencesScreen() {
    val backstack = LocalBackStack.current
    val preferences = koinInject<MangaBakaPreferences>()

    val prefLang by preferences.prefLang.collectAsState()

    MangaBakaPreferencesScreenContent(
        onBack = { backstack.removeLastOrNull() },
        prefLang = prefLang,
        onPrefLangClicked = preferences.prefLang::set,
    )
}

@Composable
private fun MangaBakaPreferencesScreenContent(
    onBack: () -> Unit,
    prefLang: LangPrefEnum,
    onPrefLangClicked: (LangPrefEnum) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_mangabaka_title)) },
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
            item {
                val themeEntries = remember { LangPrefEnum.entries.map { it to it.titleRes } }
                SettingsListItem(
                    title = stringResource(R.string.pref_mangabaka_title_lang),
                    itemSize = 1,
                    index = 0,
                    supportingContent = {
                        ButtonGroup(
                            entries = themeEntries,
                            selected = prefLang,
                            onSelect = onPrefLangClicked,
                        )
                    },
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun MangaBakaPreferencesScreenPreview() {
    PreviewContent {
        MangaBakaPreferencesScreenContent(
            onBack = { },
            prefLang = LangPrefEnum.Romaji,
            onPrefLangClicked = { },
        )
    }
}
