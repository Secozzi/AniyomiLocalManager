package xyz.secozzi.aniyomilocalmanager.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity

@Dao
interface AnimeTrackerDao {
    @Upsert
    suspend fun upsert(animeTrackerEntity: AnimeTrackerEntity)

    @Query("SELECT * FROM AnimeTrackerEntity WHERE path = :path LIMIT 1")
    fun getTrackData(path: String): Flow<AnimeTrackerEntity?>
}
