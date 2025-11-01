package xyz.secozzi.aniyomilocalmanager.data.search.anilist

import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository

class AnilistAnimeSearch(
    private val anilistSearch: AnilistSearch,
) : SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        return anilistSearch.search(query, AnilistSearchType.ANIME)
    }
}
