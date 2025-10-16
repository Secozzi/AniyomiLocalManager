package xyz.secozzi.aniyomilocalmanager.database.data

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

class MangaTrackerRepositoryImpl(
    private val database: ALMDatabase,
) : MangaTrackerRepository {
    override suspend fun upsert(mangaTrackerEntity: MangaTrackerEntity) {
        database.mangaDao().upsert(mangaTrackerEntity)
    }

    override fun getTrackData(path: String): Flow<MangaTrackerEntity?> {
        return database.mangaDao().getTrackData(path)
    }
}
