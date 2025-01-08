package xyz.secozzi.aniyomilocalmanager.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearch
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverRepository
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.preferences.preference.AndroidPreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore

val RepositoryModule = module {
    single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)

    single { AnilistSearch(get(), get()) }
    single { SearchRepositoryManager(get(), get(), get()) }

    single { CoverRepository(get(), get(), get(), get()) }
    single { EpisodeRepository(get(), get()) }
}
