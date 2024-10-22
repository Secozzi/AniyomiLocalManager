package xyz.secozzi.aniyomilocalmanager.ui.home.tabs

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences

class MangaTabScreenModel(
    private val preferences: GeneralPreferences,
) : ScreenModel {
    val storageLocation = MutableStateFlow(preferences.mangaStorageLocation.get())

    fun updateStorageLocation(newLocation: String) {
        preferences.mangaStorageLocation.set(newLocation)
        storageLocation.update { _ -> newLocation }
    }
}
