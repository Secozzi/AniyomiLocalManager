package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist

import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.mal.model.MyAnimeListSearchType
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository

class MyAnimeListMangaSearch(
    private val myAnimeListSearch: MyAnimeListSearch,
) : SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        return myAnimeListSearch.search(query, MyAnimeListSearchType.MANGA)
    }

    override suspend fun getFromId(id: String): EntryDetails {
        val id = id.toLongOrNull() ?: return EntryDetails.Empty
        return myAnimeListSearch.getFromId(id, MyAnimeListSearchType.MANGA)
    }
}
