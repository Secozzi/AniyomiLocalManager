package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum

class AniListPreferences(preferenceStore: PreferenceStore) {
    val titleLang = preferenceStore.getEnum("anilist_title_lang", TitleLangs.Romaji)
    val studioCount = preferenceStore.getInt("anilist_studio_count", 2)
}

enum class TitleLangs(name: String) {
    English("english"),
    Romaji("romaji"),
    Native("native"),
}
