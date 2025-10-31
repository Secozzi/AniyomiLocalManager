package xyz.secozzi.aniyomilocalmanager.database.data

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

class MangaTrackerRepositoryImpl(
    private val database: ALMDatabase,
) : MangaTrackerRepository {
    override suspend fun upsert(mangaTrackerEntity: MangaTrackerEntity) {
        val insert = database.mangaDao().insert(mangaTrackerEntity)
        if (insert == -1L) {
            database.mangaDao().update(
                path = mangaTrackerEntity.path,
                mangabaka = mangaTrackerEntity.mangabaka,
                anilist = mangaTrackerEntity.anilist,
                mal = mangaTrackerEntity.mal,
            )
        }
    }

    override fun getTrackData(path: String): Flow<MangaTrackerEntity?> {
        return database.mangaDao().getTrackData(path)
    }

    override suspend fun clearTrackerIds() {
        database.mangaDao().clearTrackerIds()
    }
}
