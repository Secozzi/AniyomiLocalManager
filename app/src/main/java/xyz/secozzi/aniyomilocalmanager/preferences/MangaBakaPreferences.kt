package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.getEnum

class MangaBakaPreferences(preferenceStore: PreferenceStore) {
    val prefLang = preferenceStore.getEnum("mb_pref_lang", LangPrefEnum.English)
}
