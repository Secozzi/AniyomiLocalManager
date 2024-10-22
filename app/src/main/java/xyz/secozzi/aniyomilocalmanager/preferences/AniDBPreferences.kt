package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore

class AniDBPreferences(preferenceStore: PreferenceStore) {
    val nameFormat = preferenceStore.getString("anidb_name_format", "Ep. %ep - %eng")
    val scanlatorFormat = preferenceStore.getString("anidb_scanlator_format")
}
