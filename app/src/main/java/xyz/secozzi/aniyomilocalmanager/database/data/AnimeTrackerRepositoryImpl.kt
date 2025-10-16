package xyz.secozzi.aniyomilocalmanager.database.data

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity

class AnimeTrackerRepositoryImpl(
    private val database: ALMDatabase,
) : AnimeTrackerRepository {
    override suspend fun upsert(animeTrackerEntity: AnimeTrackerEntity) {
        database.animeDao().upsert(animeTrackerEntity)
    }

    override fun getTrackData(path: String): Flow<AnimeTrackerEntity?> {
        return database.animeDao().getTrackData(path)
    }
}
