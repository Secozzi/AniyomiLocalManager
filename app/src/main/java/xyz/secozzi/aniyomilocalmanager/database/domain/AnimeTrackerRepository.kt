package xyz.secozzi.aniyomilocalmanager.database.domain

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity

interface AnimeTrackerRepository {
    suspend fun upsert(animeTrackerEntity: AnimeTrackerEntity)

    fun getTrackData(path: String): Flow<AnimeTrackerEntity?>

    suspend fun clearTrackerIds()
}
