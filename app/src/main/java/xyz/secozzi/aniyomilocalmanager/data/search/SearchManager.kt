package xyz.secozzi.aniyomilocalmanager.data.search

import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistAnimeSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistMangaSearch
import xyz.secozzi.aniyomilocalmanager.data.search.mangabaka.MangaBakaSearch
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds

class SearchManager(
    private val mangaBakaSearch: MangaBakaSearch,
    private val anilistAnimeSearch: AnilistAnimeSearch,
    private val anilistMangaSearch: AnilistMangaSearch,
) {
    fun getSearchRepository(id: SearchIds): SearchRepository {
        return when (id) {
            SearchIds.MangaBaka -> mangaBakaSearch
            SearchIds.AnilistAnime -> anilistAnimeSearch
            SearchIds.AnilistManga -> anilistMangaSearch
            else -> throw Exception("Invalid search repository: $id")
        }
    }
}
