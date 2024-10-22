package xyz.secozzi.aniyomilocalmanager.data.anilist

import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepository
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences

class AnilistMangaSearchRepository(
    override val id: Long,
    private val anilistSearch: AnilistSearch,
    private val aniListPreferences: AniListPreferences,
) : SearchRepository {
    override suspend fun search(query: String): List<SearchDataItem> {
        val data = anilistSearch.search(query, AnilistSearchType.MANGA)

        return data.map {
            it.toALManga(aniListPreferences.titleLang.get())
        }
    }
}