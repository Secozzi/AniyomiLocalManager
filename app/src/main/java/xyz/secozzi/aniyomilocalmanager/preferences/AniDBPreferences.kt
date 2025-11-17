package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum

class AniDBPreferences(preferenceStore: PreferenceStore) {
    val prefLang = preferenceStore.getEnum("anidb_title_lang", LangPrefEnum.English)
    val nameFormat = preferenceStore.getString("anidb_name_format", "Ep. %ep - %eng")
    val scanlatorFormat = preferenceStore.getString("anidb_scanlator_format", "")
    val summaryFormat = preferenceStore.getString("anidb_summary_format", "%sum")
}
