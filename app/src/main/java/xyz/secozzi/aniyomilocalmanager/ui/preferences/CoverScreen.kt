package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsSwitch
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data object CoverPreferencesRoute : NavKey

@Composable
fun CoverPreferencesScreen() {
    val backstack = LocalBackStack.current
    val preferences = koinInject<CoverPreferences>()

    val animeAnilist by preferences.animeCoverAnilist.collectAsState()
    val animeMal by preferences.animeCoverMal.collectAsState()
    val animeFanart by preferences.animeCoverFanart.collectAsState()

    val mangaAnilist by preferences.mangaCoverAnilist.collectAsState()
    val mangaMal by preferences.mangaCoverMal.collectAsState()
    val mangaMangadex by preferences.mangaCoverMangadex.collectAsState()

    val gridSize by preferences.gridSize.collectAsState()

    CoverPreferencesScreenContent(
        onBack = { backstack.removeLastOrNull() },
        animeAnilist = animeAnilist,
        onAnimeAnilistClicked = preferences.animeCoverAnilist::set,
        animeMal = animeMal,
        onAnimeMalClicked = preferences.animeCoverMal::set,
        animeFanart = animeFanart,
        onAnimeFanartClicked = preferences.animeCoverFanart::set,
        mangaAnilist = mangaAnilist,
        onMangaAnilistClicked = preferences.mangaCoverAnilist::set,
        mangaMal = mangaMal,
        onMangaMalClicked = preferences.mangaCoverMal::set,
        mangaMangadex = mangaMangadex,
        onMangaMangadexClicked = preferences.mangaCoverMangadex::set,
        gridSize = gridSize,
        onGridSizeChange = preferences.gridSize::set,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CoverPreferencesScreenContent(
    onBack: () -> Unit,
    animeAnilist: Boolean,
    onAnimeAnilistClicked: (Boolean) -> Unit,
    animeMal: Boolean,
    onAnimeMalClicked: (Boolean) -> Unit,
    animeFanart: Boolean,
    onAnimeFanartClicked: (Boolean) -> Unit,
    mangaAnilist: Boolean,
    onMangaAnilistClicked: (Boolean) -> Unit,
    mangaMal: Boolean,
    onMangaMalClicked: (Boolean) -> Unit,
    mangaMangadex: Boolean,
    onMangaMangadexClicked: (Boolean) -> Unit,
    gridSize: Int,
    onGridSizeChange: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_cover_title)) },
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
            // Anime
            item {
                Text(
                    text = stringResource(R.string.label_anime),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.smaller),
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_anilist_title),
                    icon = ImageVector.vectorResource(R.drawable.anilist_icon),
                    itemSize = 3,
                    index = 0,
                    trailingContent = {
                        SettingsSwitch(
                            checked = animeAnilist,
                            onClick = onAnimeAnilistClicked,
                        )
                    },
                )
            }

            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_mal_title),
                    icon = ImageVector.vectorResource(R.drawable.mal_icon),
                    itemSize = 3,
                    index = 1,
                    trailingContent = {
                        SettingsSwitch(
                            checked = animeMal,
                            onClick = onAnimeMalClicked,
                        )
                    },
                )
            }

            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_fanart_title),
                    icon = Icons.Filled.Tv,
                    itemSize = 3,
                    index = 2,
                    trailingContent = {
                        SettingsSwitch(
                            checked = animeFanart,
                            onClick = onAnimeFanartClicked,
                        )
                    },
                )
            }

            item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

            // Manga
            item {
                Text(
                    text = stringResource(R.string.label_manga),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.smaller),
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_anilist_title),
                    icon = ImageVector.vectorResource(R.drawable.anilist_icon),
                    itemSize = 3,
                    index = 0,
                    trailingContent = {
                        SettingsSwitch(
                            checked = mangaAnilist,
                            onClick = onMangaAnilistClicked,
                        )
                    },
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_mal_title),
                    icon = ImageVector.vectorResource(R.drawable.mal_icon),
                    itemSize = 3,
                    index = 1,
                    trailingContent = {
                        SettingsSwitch(
                            checked = mangaMal,
                            onClick = onMangaMalClicked,
                        )
                    },
                )
            }
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_mangadex_title),
                    icon = ImageVector.vectorResource(R.drawable.md_icon),
                    itemSize = 3,
                    index = 2,
                    trailingContent = {
                        SettingsSwitch(
                            checked = mangaMangadex,
                            onClick = onMangaMangadexClicked,
                        )
                    },
                )
            }

            item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

            // Display
            item {
                Text(
                    text = stringResource(R.string.pref_display_label),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.smaller),
                )
            }

            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_grid_size),
                    icon = Icons.Filled.GridOn,
                    itemSize = 1,
                    index = 0,
                    supportingContent = {
                        Column {
                            Text(gridSize.toString())
                            Slider(
                                value = gridSize.toFloat(),
                                onValueChange = { onGridSizeChange(it.toInt()) },
                                valueRange = 1f..10f,
                                steps = 8,
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun CoverPreferencesScreenContentPreview() {
    PreviewContent {
        CoverPreferencesScreenContent(
            onBack = { },
            animeAnilist = true,
            onAnimeAnilistClicked = { },
            animeMal = true,
            onAnimeMalClicked = { },
            animeFanart = false,
            onAnimeFanartClicked = { },
            mangaAnilist = true,
            onMangaAnilistClicked = { },
            mangaMal = true,
            onMangaMalClicked = { },
            mangaMangadex = true,
            onMangaMangadexClicked = { },
            gridSize = 3,
            onGridSizeChange = { },
        )
    }
}
