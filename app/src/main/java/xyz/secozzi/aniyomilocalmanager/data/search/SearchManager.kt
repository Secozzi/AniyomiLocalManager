package xyz.secozzi.aniyomilocalmanager.data.search

import xyz.secozzi.aniyomilocalmanager.data.search.anidb.AniDBSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistAnimeSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistMangaSearch
import xyz.secozzi.aniyomilocalmanager.data.search.mangabaka.MangaBakaSearch
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.MyAnimeListAnimeSearch
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.MyAnimeListMangaSearch
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.repository.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds

class SearchManager(
    private val myAnimeListAnimeSearch: MyAnimeListAnimeSearch,
    private val myAnimeListMangaSearch: MyAnimeListMangaSearch,
    private val mangaBakaSearch: MangaBakaSearch,
    private val anilistAnimeSearch: AnilistAnimeSearch,
    private val anilistMangaSearch: AnilistMangaSearch,
    private val anidbSearch: AniDBSearch,
) {
    fun getSearchRepository(id: SearchIds): SearchRepository {
        return when (id) {
            SearchIds.MangaBaka -> mangaBakaSearch
            SearchIds.AniDB -> anidbSearch
            SearchIds.AnilistAnime -> anilistAnimeSearch
            SearchIds.AnilistManga -> anilistMangaSearch
            SearchIds.MalAnime -> myAnimeListAnimeSearch
            SearchIds.MalManga -> myAnimeListMangaSearch
        }
    }

    fun getEpisodeRepository(id: SearchIds): EpisodeRepository {
        return when (id) {
            SearchIds.AniDB -> anidbSearch
            SearchIds.MalAnime -> myAnimeListAnimeSearch
            else -> throw Exception("Invalid search repository: $id")
        }
    }
}
