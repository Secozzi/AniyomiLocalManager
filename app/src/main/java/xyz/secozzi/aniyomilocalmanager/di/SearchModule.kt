package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.data.search.anidb.AniDBSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistAnimeSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistMangaSearch
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.AnilistSearch
import xyz.secozzi.aniyomilocalmanager.data.search.mangabaka.MangaBakaSearch
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.MyAnimeListAnimeSearch

val SearchModule = module {
    singleOf(::MangaBakaSearch)
    singleOf(::AnilistSearch)
    singleOf(::AnilistAnimeSearch)
    singleOf(::AnilistMangaSearch)
    singleOf(::MyAnimeListAnimeSearch)
    singleOf(::AniDBSearch)
    singleOf(::SearchManager)
}
