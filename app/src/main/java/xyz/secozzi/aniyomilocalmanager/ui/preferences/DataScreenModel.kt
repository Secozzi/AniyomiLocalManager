package xyz.secozzi.aniyomilocalmanager.ui.preferences

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences

class DataScreenModel(
    private val preferences: GeneralPreferences,
    private val trackerIdRepository: TrackerIdRepository,
) : ScreenModel {
    fun clearLocalAnime() {
        preferences.animeStorageLocation.delete()
    }

    fun clearLocalManga() {
        preferences.mangaStorageLocation.delete()
    }

    fun clearTrackerIds() {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.clearTrackerIds()
        }
    }
}
