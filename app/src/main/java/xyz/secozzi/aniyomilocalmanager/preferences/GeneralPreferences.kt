package xyz.secozzi.aniyomilocalmanager.preferences

import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore

class GeneralPreferences(preferenceStore: PreferenceStore) {
    val animeStorageLocation = preferenceStore.getString("anime_storage_location")
    val mangaStorageLocation = preferenceStore.getString("manga_storage_location")
}
