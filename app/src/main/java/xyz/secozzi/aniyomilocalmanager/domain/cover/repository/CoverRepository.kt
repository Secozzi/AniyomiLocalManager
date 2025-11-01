package xyz.secozzi.aniyomilocalmanager.domain.cover.repository

import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData

interface CoverRepository {
    suspend fun getMangaCovers(
        trackerEntity: MangaTrackerEntity,
        anilist: Boolean,
        mal: Boolean,
        mangadex: Boolean,
    ): List<CoverData>

    suspend fun getAnimeCovers(
        trackerEntity: AnimeTrackerEntity,
        anilist: Boolean,
        mal: Boolean,
        fanart: Boolean,
    ): List<CoverData>
}
