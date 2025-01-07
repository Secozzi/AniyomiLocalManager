package xyz.secozzi.aniyomilocalmanager.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity

@Dao
interface TrackerIdDao {
    @Upsert
    suspend fun upsert(trackerIdEntity: TrackerIdEntity)

    @Query("SELECT EXISTS(SELECT * FROM TrackerIdEntity WHERE path = :path)")
    suspend fun exists(path: String): Boolean

    @Query("SELECT * FROM TrackerIdEntity WHERE path = :path LIMIT 1")
    fun getTrackerId(path: String): Flow<TrackerIdEntity?>

    @Query("UPDATE TrackerIdEntity SET anilistId = :anilistId WHERE path = :path")
    suspend fun updateAniListId(path: String, anilistId: Long)

    @Query("UPDATE TrackerIdEntity SET aniDBId = :aniDBId WHERE path = :path")
    suspend fun updateAniDBId(path: String, aniDBId: Long)

    @Query("DELETE FROM TrackerIdEntity")
    suspend fun clearTrackerIds()
}
