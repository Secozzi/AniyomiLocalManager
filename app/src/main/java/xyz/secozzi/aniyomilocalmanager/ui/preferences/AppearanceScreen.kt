package xyz.secozzi.aniyomilocalmanager.ui.preferences

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.AppearancePreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ButtonGroup
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ButtonGroupEntry
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsSwitch
import xyz.secozzi.aniyomilocalmanager.ui.theme.DarkMode
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data object AppearancePreferencesRoute : NavKey

@Composable
fun AppearancePreferencesScreen() {
    val backstack = LocalBackStack.current
    val preferences = koinInject<AppearancePreferences>()

    val darkMode by preferences.darkMode.collectAsState()
    val isMaterialYou by preferences.materialYou.collectAsState()
    val showInfo by preferences.showInfo.collectAsState()
    val isAnimeEnabled by preferences.animeIsEnabled.collectAsState()
    val isMangaEnabled by preferences.mangaIsEnabled.collectAsState()

    AppearancePreferencesScreenContent(
        onBack = { backstack.removeLastOrNull() },
        darkMode = darkMode,
        onDarkModeClicked = preferences.darkMode::set,
        isMaterialYou = isMaterialYou,
        onMaterialYouClicked = preferences.materialYou::set,
        showInfo = showInfo,
        onShowInfoClicked = preferences.showInfo::set,
        isAnimeEnabled = isAnimeEnabled,
        onAnimeEnabledClicked = preferences.animeIsEnabled::set,
        isMangaEnabled = isMangaEnabled,
        onMangaEnabledClicked = preferences.mangaIsEnabled::set,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppearancePreferencesScreenContent(
    onBack: () -> Unit,
    darkMode: DarkMode,
    onDarkModeClicked: (DarkMode) -> Unit,
    isMaterialYou: Boolean,
    onMaterialYouClicked: (Boolean) -> Unit,
    showInfo: Boolean,
    onShowInfoClicked: (Boolean) -> Unit,
    isAnimeEnabled: Boolean,
    onAnimeEnabledClicked: (Boolean) -> Unit,
    isMangaEnabled: Boolean,
    onMangaEnabledClicked: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_appearance_title)) },
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
            // Theme
            item {
                val themeEntries = remember {
                    DarkMode.entries.map { ButtonGroupEntry(it, it.titleRes) }.toPersistentList()
                }
                val selected = remember(darkMode) {
                    ButtonGroupEntry(darkMode, darkMode.titleRes)
                }

                SettingsListItem(
                    title = stringResource(R.string.pref_appearance_theme),
                    itemSize = 2,
                    index = 0,
                    supportingContent = {
                        ButtonGroup(
                            entries = themeEntries,
                            selected = selected,
                            onSelect = onDarkModeClicked,
                        )
                    },
                )
            }
            item {
                val isMaterialYouAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                SettingsListItem(
                    title = stringResource(R.string.pref_appearance_material_you_title),
                    icon = Icons.Outlined.Brush,
                    itemSize = 2,
                    index = 1,
                    supportingContent = {
                        Text(
                            text = stringResource(
                                if (isMaterialYouAvailable) {
                                    R.string.pref_appearance_material_you_summary
                                } else {
                                    R.string.pref_appearance_material_you_summary_disabled
                                },
                            ),
                        )
                    },
                    trailingContent = {
                        SettingsSwitch(
                            checked = isMaterialYou,
                            enabled = isMaterialYouAvailable,
                            onClick = onMaterialYouClicked,
                        )
                    },
                )
            }

            item { Spacer(Modifier.height(8.dp)) }

            // Display
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_appearance_show_info),
                    icon = Icons.Outlined.Info,
                    itemSize = 3,
                    index = 0,
                    trailingContent = {
                        SettingsSwitch(
                            checked = showInfo,
                            onClick = onShowInfoClicked,
                        )
                    },
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_appearance_display_anime),
                    icon = Icons.Outlined.PlayCircleOutline,
                    itemSize = 3,
                    index = 1,
                    trailingContent = {
                        SettingsSwitch(
                            checked = isAnimeEnabled,
                            enabled = isMangaEnabled,
                            onClick = onAnimeEnabledClicked,
                        )
                    },
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_appearance_display_manga),
                    icon = Icons.AutoMirrored.Filled.ChromeReaderMode,
                    itemSize = 3,
                    index = 2,
                    trailingContent = {
                        SettingsSwitch(
                            checked = isMangaEnabled,
                            enabled = isAnimeEnabled,
                            onClick = onMangaEnabledClicked,
                        )
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun AppearancePreferencesScreenPreview() {
    PreviewContent {
        AppearancePreferencesScreenContent(
            onBack = {},
            darkMode = DarkMode.System,
            onDarkModeClicked = {},
            isMaterialYou = true,
            onMaterialYouClicked = {},
            showInfo = true,
            onShowInfoClicked = {},
            isAnimeEnabled = true,
            onAnimeEnabledClicked = {},
            isMangaEnabled = true,
            onMangaEnabledClicked = {},
        )
    }
}
