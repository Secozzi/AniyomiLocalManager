package xyz.secozzi.aniyomilocalmanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

@Dao
interface MangaTrackerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mangaTrackerEntity: MangaTrackerEntity): Long

    @Query(
        """
        UPDATE MangaTrackerEntity SET
            mangabaka = coalesce(:mangabaka, mangabaka),
            anilist = coalesce(:anilist, anilist),
            mal = coalesce(:mal, mal)
        WHERE path = :path
    """,
    )
    suspend fun update(path: String, mangabaka: Long? = null, anilist: Long? = null, mal: Long? = null)

    @Query("SELECT * FROM MangaTrackerEntity WHERE path = :path LIMIT 1")
    fun getTrackData(path: String): Flow<MangaTrackerEntity?>

    @Query("DELETE FROM MangaTrackerEntity")
    suspend fun clearTrackerIds()
}
