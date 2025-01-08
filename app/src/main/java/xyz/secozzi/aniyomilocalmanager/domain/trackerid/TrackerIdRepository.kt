package xyz.secozzi.aniyomilocalmanager.domain.trackerid

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity

interface TrackerIdRepository {
    suspend fun upsert(trackerIdEntity: TrackerIdEntity)

    suspend fun exists(path: String): Boolean

    fun getTrackerId(path: String): Flow<TrackerIdEntity?>

    suspend fun updateAniListId(path: String, anilistId: Long)

    suspend fun updateAniDBId(path: String, aniDBId: Long)

    suspend fun clearTrackerIds()
}
