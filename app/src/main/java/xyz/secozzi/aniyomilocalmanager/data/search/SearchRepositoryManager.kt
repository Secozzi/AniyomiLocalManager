package xyz.secozzi.aniyomilocalmanager.data.search

import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.data.anidb.search.AniDBSearchRepository
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistAnimeSearchRepository
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistMangaSearchRepository
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearch
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences

class SearchRepositoryManager(
    private val okHttpClient: OkHttpClient,
    private val anilistSearch: AnilistSearch,
    private val aniListPreferences: AniListPreferences,
) {
    companion object {
        const val ANILIST_MANGA = 1L
        const val ANILIST_ANIME = 2L
        const val ANIDB = 3L
    }

    val alMangaRepo = AnilistMangaSearchRepository(ANILIST_MANGA, anilistSearch, aniListPreferences)
    val alAnimeRepo = AnilistAnimeSearchRepository(ANILIST_ANIME, anilistSearch, aniListPreferences)
    val adbRepo = AniDBSearchRepository(ANIDB, okHttpClient)

    val searchRepos = listOf(
        alMangaRepo,
        alAnimeRepo,
        adbRepo,
    )

    fun getRepo(id: Long): SearchRepository {
        return searchRepos.find { it.id == id }!!
    }
}
