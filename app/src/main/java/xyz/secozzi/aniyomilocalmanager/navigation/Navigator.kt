package xyz.secozzi.aniyomilocalmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import xyz.secozzi.aniyomilocalmanager.presentation.utils.predictiveHorizonal
import xyz.secozzi.aniyomilocalmanager.presentation.utils.slideHorizontal
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryScreen
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeRoute
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeScreen
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryRoute
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AppearancePreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AppearancePreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Composable
fun Navigator() {
    val backStack = rememberNavBackStack(HomeRoute)

    CompositionLocalProvider(
        LocalBackStack provides backStack,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { slideHorizontal },
            popTransitionSpec = { predictiveHorizonal },
            predictivePopTransitionSpec = { predictiveHorizonal },
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    HomeScreen()
                }

                // Anime
                entry<AnimeEntryRoute> { route ->
                    AnimeEntryScreen(route.path)
                }

                // Manga
                entry<MangaEntryRoute> { route ->
                    MangaEntryScreen(route.path)
                }

                // Preferences
                entry<PreferencesRoute> {
                    PreferencesScreen()
                }
                entry<AppearancePreferencesRoute> {
                    AppearancePreferencesScreen()
                }
                entry<DataPreferencesRoute> {
                    DataPreferencesScreen()
                }
            },
        )
    }
}
