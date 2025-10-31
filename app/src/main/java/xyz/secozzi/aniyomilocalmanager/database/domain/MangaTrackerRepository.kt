package xyz.secozzi.aniyomilocalmanager.database.domain

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

interface MangaTrackerRepository {
    suspend fun upsert(mangaTrackerEntity: MangaTrackerEntity)

    fun getTrackData(path: String): Flow<MangaTrackerEntity?>

    suspend fun clearTrackerIds()
}
