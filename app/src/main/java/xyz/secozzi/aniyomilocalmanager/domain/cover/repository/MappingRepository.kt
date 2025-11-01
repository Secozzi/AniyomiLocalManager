package xyz.secozzi.aniyomilocalmanager.domain.cover.repository

interface MappingRepository {
    suspend fun getTvdbId(anilistId: Long?, malId: Long?, anidbId: Long?): Pair<String, Long>?
}
