package xyz.secozzi.aniyomilocalmanager.presentation.utils

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.AppearancePreferences

sealed class TopLevelRoute(val icon: ImageVector, @param:StringRes val stringRes: Int) {
    data object Anime : TopLevelRoute(Icons.Outlined.PlayCircleOutline, R.string.label_anime)
    data object Manga : TopLevelRoute(Icons.AutoMirrored.Filled.ChromeReaderMode, R.string.label_manga)
}

class TopLevelBackStack(startKey: TopLevelRoute) {
    // Maintain a stack for each top level route
    internal var topLevelStacks: LinkedHashMap<TopLevelRoute, SnapshotStateList<TopLevelRoute>> = linkedMapOf(
        startKey to mutableStateListOf(startKey),
    )

    // Expose the current top level route for consumers
    var topLevelKey by mutableStateOf(startKey)
        private set

    // Expose the back stack so it can be rendered by the NavDisplay
    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() =
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }

    fun addTopLevel(key: TopLevelRoute) {
        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null) {
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: TopLevelRoute) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove the associated top level stack
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }

    companion object {
        fun Saver(prefs: AppearancePreferences): Saver<TopLevelBackStack, String> = Saver(
            save = { backStack ->
                when (backStack.backStack.last()) {
                    TopLevelRoute.Anime -> "anime"
                    TopLevelRoute.Manga -> "manga"
                }
            },
            restore = { stringRoute ->
                val route = when (stringRoute) {
                    "anime" -> TopLevelRoute.Anime
                    "manga" -> TopLevelRoute.Manga
                    else -> throw IllegalArgumentException("Invalid route: $stringRoute")
                }

                val showAnime = prefs.animeIsEnabled.get()
                val showManga = prefs.mangaIsEnabled.get()

                val startRoute = when (route) {
                    TopLevelRoute.Anime if !showAnime -> TopLevelRoute.Manga
                    TopLevelRoute.Manga if !showManga -> TopLevelRoute.Anime
                    else -> route
                }

                val map = buildMap<TopLevelRoute, SnapshotStateList<TopLevelRoute>>(2) {
                    if (startRoute == TopLevelRoute.Anime || showAnime) {
                        put(TopLevelRoute.Anime, mutableStateListOf(TopLevelRoute.Anime))
                    }
                    if (startRoute == TopLevelRoute.Manga) {
                        put(TopLevelRoute.Manga, mutableStateListOf(TopLevelRoute.Manga))
                    }
                }.toMap(LinkedHashMap())

                TopLevelBackStack(startRoute).apply {
                    topLevelStacks = map
                    updateBackStack()
                }
            },
        )
    }
}
