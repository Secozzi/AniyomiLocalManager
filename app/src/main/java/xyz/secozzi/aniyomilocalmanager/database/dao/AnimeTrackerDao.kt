package xyz.secozzi.aniyomilocalmanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity

@Dao
interface AnimeTrackerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackerEntity: AnimeTrackerEntity): Long

    @Query(
        """
        UPDATE AnimeTrackerEntity SET
            anilist = coalesce(:anilist, anilist),
            anidb = coalesce(:anidb, anidb),
            mal = coalesce(:mal, mal)
        WHERE path = :path
    """,
    )
    suspend fun update(path: String, anilist: Long? = null, anidb: Long? = null, mal: Long? = null)

    @Query("SELECT * FROM AnimeTrackerEntity WHERE path = :path LIMIT 1")
    fun getTrackData(path: String): Flow<AnimeTrackerEntity?>

    @Query("DELETE FROM AnimeTrackerEntity")
    suspend fun clearTrackerIds()
}
