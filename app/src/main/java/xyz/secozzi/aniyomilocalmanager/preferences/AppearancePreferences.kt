package xyz.secozzi.aniyomilocalmanager.preferences

import android.os.Build
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum
import xyz.secozzi.aniyomilocalmanager.ui.theme.DarkMode

class AppearancePreferences(preferenceStore: PreferenceStore) {
    val darkMode = preferenceStore.getEnum("dark_mode", DarkMode.System)
    val materialYou = preferenceStore.getBoolean("material_you", Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    val animeIsEnabled = preferenceStore.getBoolean("anime_enabled", true)
    val mangaIsEnabled = preferenceStore.getBoolean("manga_enabled", true)
}
