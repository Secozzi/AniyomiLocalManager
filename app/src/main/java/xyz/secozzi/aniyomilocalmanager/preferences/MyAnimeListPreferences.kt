package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum

class MyAnimeListPreferences(preferenceStore: PreferenceStore) {
    val prefLang = preferenceStore.getEnum("mal_pref_lang", LangPrefEnum.English)
    val nameFormat = preferenceStore.getString("mal_name_format", "Ep. %ep - %eng")
    val scanlatorFormat = preferenceStore.getString("mal_scanlator_format", "")
    val summaryFormat = preferenceStore.getString("mal_summary_format", "")
}
