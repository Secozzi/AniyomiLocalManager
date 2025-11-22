package xyz.secozzi.aniyomilocalmanager.database.data

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity

class AnimeTrackerRepositoryImpl(
    private val database: ALMDatabase,
) : AnimeTrackerRepository {
    override suspend fun upsert(animeTrackerEntity: AnimeTrackerEntity) {
        val insert = database.animeDao().insert(animeTrackerEntity)
        if (insert == -1L) {
            database.animeDao().update(
                path = animeTrackerEntity.path,
                anilist = animeTrackerEntity.anilist,
                anidb = animeTrackerEntity.anidb,
                mal = animeTrackerEntity.mal,
            )
        }
    }

    override fun getTrackData(path: String): Flow<AnimeTrackerEntity?> {
        return database.animeDao().getTrackData(path)
    }

    override suspend fun clearTrackerIds() {
        database.animeDao().clearTrackerIds()
    }
}
