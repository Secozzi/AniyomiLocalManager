package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore

class CoverPreferences(preferenceStore: PreferenceStore) {
    val animeCoverAnilist = preferenceStore.getBoolean("anime_cover_anilist", true)
    val animeCoverMAL = preferenceStore.getBoolean("anime_cover_mal", true)
    val animeCoverFanart = preferenceStore.getBoolean("anime_cover_fanart", true)

    val mangaCoverAnilist = preferenceStore.getBoolean("manga_cover_anilist", true)
    val mangaCoverMAL = preferenceStore.getBoolean("manga_cover_mal", true)
    val mangaCoverMD = preferenceStore.getBoolean("manga_cover_md", true)

    val gridSize = preferenceStore.getInt("cover_grid_size", 3)
}
