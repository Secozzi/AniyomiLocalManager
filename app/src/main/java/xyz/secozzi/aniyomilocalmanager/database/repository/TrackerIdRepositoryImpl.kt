package xyz.secozzi.aniyomilocalmanager.database.repository

import kotlinx.coroutines.flow.Flow
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository

class TrackerIdRepositoryImpl(
    private val database: ALMDatabase,
) : TrackerIdRepository {
    override suspend fun upsert(trackerIdEntity: TrackerIdEntity) {
        database.trackerIdDao().upsert(trackerIdEntity)
    }

    override suspend fun exists(path: String): Boolean {
        return database.trackerIdDao().exists(path)
    }

    override fun getTrackerId(path: String): Flow<TrackerIdEntity?> {
        return database.trackerIdDao().getTrackerId(path)
    }

    override suspend fun updateAniListId(path: String, anilistId: Long) {
        if (exists(path)) {
            database.trackerIdDao().updateAniListId(path, anilistId)
        } else {
            database.trackerIdDao().upsert(
                TrackerIdEntity(
                    path = path,
                    anilistId = anilistId,
                ),
            )
        }
    }

    override suspend fun updateAniDBId(path: String, aniDBId: Long) {
        if (exists(path)) {
            database.trackerIdDao().updateAniDBId(path, aniDBId)
        } else {
            database.trackerIdDao().upsert(
                TrackerIdEntity(
                    path = path,
                    aniDBId = aniDBId,
                ),
            )
        }
    }

    override suspend fun clearTrackerIds() {
        database.trackerIdDao().clearTrackerIds()
    }
}
