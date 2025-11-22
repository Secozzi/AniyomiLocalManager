package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum

class AnilistPreferences(preferenceStore: PreferenceStore) {
    val prefLang = preferenceStore.getEnum("anilist_title_lang", LangPrefEnum.English)
    val studioCount = preferenceStore.getInt("anilist_studio_count", 2)
}
