package xyz.secozzi.aniyomilocalmanager.ui.home.tabs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.asState

class MangaTabScreenModel(
    private val preferences: GeneralPreferences,
) : ScreenModel {
    var storageLocation by preferences.mangaStorageLocation
        .asState(screenModelScope)

    fun updateStorageLocation(newLocation: String) {
        storageLocation = newLocation
    }
}
