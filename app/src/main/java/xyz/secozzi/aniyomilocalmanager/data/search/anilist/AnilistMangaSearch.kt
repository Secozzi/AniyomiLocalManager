package xyz.secozzi.aniyomilocalmanager.data.search.anilist

import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository

class AnilistMangaSearch(
    private val anilistSearch: AnilistSearch,
) : SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        return anilistSearch.search(query, AnilistSearchType.MANGA)
    }

    override suspend fun getFromId(id: String): EntryDetails {
        TODO("Not yet implemented")
    }
}
