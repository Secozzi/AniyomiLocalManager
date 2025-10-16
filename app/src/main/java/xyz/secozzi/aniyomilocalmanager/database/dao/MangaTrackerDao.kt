package xyz.secozzi.aniyomilocalmanager.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

@Dao
interface MangaTrackerDao {
    @Upsert
    suspend fun upsert(mangaTrackerEntity: MangaTrackerEntity)

    @Query("SELECT * FROM MangaTrackerEntity WHERE path = :path LIMIT 1")
    fun getTrackData(path: String): Flow<MangaTrackerEntity?>
}
