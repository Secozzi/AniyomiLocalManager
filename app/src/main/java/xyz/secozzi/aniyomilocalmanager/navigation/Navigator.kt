package xyz.secozzi.aniyomilocalmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import xyz.secozzi.aniyomilocalmanager.presentation.utils.popSpec
import xyz.secozzi.aniyomilocalmanager.presentation.utils.transitionSpec
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverScreen
import xyz.secozzi.aniyomilocalmanager.ui.anime.details.AnimeDetailsRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.details.AnimeDetailsScreen
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryScreen
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit.AnimeEditEpisodesRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.edit.AnimeEditEpisodesScreen
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch.AnimeFetchEpisodesRoute
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch.AnimeFetchEpisodesScreen
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeRoute
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeScreen
import xyz.secozzi.aniyomilocalmanager.ui.manga.chapters.MangaChaptersRoute
import xyz.secozzi.aniyomilocalmanager.ui.manga.chapters.MangaChaptersScreen
import xyz.secozzi.aniyomilocalmanager.ui.manga.cover.MangaCoverRoute
import xyz.secozzi.aniyomilocalmanager.ui.manga.cover.MangaCoverScreen
import xyz.secozzi.aniyomilocalmanager.ui.manga.details.MangaDetailsRoute
import xyz.secozzi.aniyomilocalmanager.ui.manga.details.MangaDetailsScreen
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryRoute
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniDBPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniDBPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AnilistPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AnilistPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AppearancePreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AppearancePreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.CoverPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.CoverPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.MangaBakaPreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.MangaBakaPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchRoute
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack
import xyz.secozzi.aniyomilocalmanager.utils.LocalResultStore
import xyz.secozzi.aniyomilocalmanager.utils.rememberResultStore

@Composable
fun Navigator() {
    val backStack = rememberNavBackStack(HomeRoute)
    val resultStore = rememberResultStore()

    CompositionLocalProvider(
        LocalBackStack provides backStack,
        LocalResultStore provides resultStore,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { transitionSpec },
            popTransitionSpec = { popSpec },
            predictivePopTransitionSpec = { popSpec },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                // Home
                entry<HomeRoute> {
                    HomeScreen()
                }

                // Anime
                entry<AnimeEntryRoute> { route ->
                    AnimeEntryScreen(route.path)
                }
                entry<AnimeCoverRoute> { route ->
                    AnimeCoverScreen(route.path)
                }
                entry<AnimeDetailsRoute> { route ->
                    AnimeDetailsScreen(route.path)
                }
                entry<AnimeFetchEpisodesRoute> { route ->
                    AnimeFetchEpisodesScreen(route.path)
                }
                entry<AnimeEditEpisodesRoute> { route ->
                    AnimeEditEpisodesScreen(route.path)
                }

                // Manga
                entry<MangaEntryRoute> { route ->
                    MangaEntryScreen(route.path)
                }
                entry<MangaCoverRoute> { route ->
                    MangaCoverScreen(route.path)
                }
                entry<MangaDetailsRoute> { route ->
                    MangaDetailsScreen(route.path)
                }
                entry<MangaChaptersRoute> { route ->
                    MangaChaptersScreen(route.path)
                }

                // Search
                entry<SearchRoute> { route ->
                    SearchScreen(route.query, route.searchRepositoryId)
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
                entry<MangaBakaPreferencesRoute> {
                    MangaBakaPreferencesScreen()
                }
                entry<AnilistPreferencesRoute> {
                    AnilistPreferencesScreen()
                }
                entry<AniDBPreferencesRoute> {
                    AniDBPreferencesScreen()
                }
                entry<CoverPreferencesRoute> {
                    CoverPreferencesScreen()
                }
            },
        )
    }
}
