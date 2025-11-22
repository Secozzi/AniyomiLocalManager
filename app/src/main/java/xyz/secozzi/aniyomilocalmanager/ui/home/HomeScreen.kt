package xyz.secozzi.aniyomilocalmanager.ui.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.AppearancePreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.utils.TopLevelBackStack
import xyz.secozzi.aniyomilocalmanager.presentation.utils.popSpec
import xyz.secozzi.aniyomilocalmanager.presentation.utils.transitionSpec
import xyz.secozzi.aniyomilocalmanager.ui.home.anime.AnimeScreen
import xyz.secozzi.aniyomilocalmanager.ui.home.manga.MangaScreen

@Serializable
data object HomeRoute : NavKey

private sealed class TopLevelRoute(val icon: ImageVector, @param:StringRes val stringRes: Int) {
    data object Anime : TopLevelRoute(Icons.Outlined.PlayCircleOutline, R.string.label_anime)
    data object Manga : TopLevelRoute(Icons.AutoMirrored.Filled.ChromeReaderMode, R.string.label_manga)
}

@Composable
fun HomeScreen() {
    val preferences = koinInject<AppearancePreferences>()

    val showAnime by preferences.animeIsEnabled.collectAsState()
    val showManga by preferences.mangaIsEnabled.collectAsState()

    val startRoute = if (!showAnime) TopLevelRoute.Manga else TopLevelRoute.Anime
    val topLevelRoutes = listOf(TopLevelRoute.Anime, TopLevelRoute.Manga)
    val topLevelBackStack = remember { TopLevelBackStack(startRoute) }

    Scaffold(
        bottomBar = {
            if (showAnime && showManga) {
                NavigationBar {
                    topLevelRoutes.forEach { topLevelRoute ->
                        val isSelected = topLevelRoute == topLevelBackStack.topLevelKey
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { topLevelBackStack.addTopLevel(topLevelRoute) },
                            label = {
                                Text(
                                    text = stringResource(topLevelRoute.stringRes),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = topLevelRoute.icon,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { mainPadding ->
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            onBack = { topLevelBackStack.removeLast() },
            transitionSpec = { transitionSpec },
            popTransitionSpec = { popSpec },
            predictivePopTransitionSpec = { popSpec },
            entryProvider = entryProvider {
                entry<TopLevelRoute.Anime> {
                    AnimeScreen(mainPadding.calculateBottomPadding())
                }
                entry<TopLevelRoute.Manga> {
                    MangaScreen(mainPadding.calculateBottomPadding())
                }
            },
        )
    }
}
