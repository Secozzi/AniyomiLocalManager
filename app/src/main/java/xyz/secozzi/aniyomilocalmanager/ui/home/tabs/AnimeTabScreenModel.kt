package xyz.secozzi.aniyomilocalmanager.ui.home.tabs

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences

class AnimeTabScreenModel(
    private val preferences: GeneralPreferences,
) : ScreenModel {
    val storageLocation = MutableStateFlow(preferences.animeStorageLocation.get())

    fun updateStorageLocation(newLocation: String) {
        preferences.animeStorageLocation.set(newLocation)
        storageLocation.update { _ -> newLocation }
    }
}
