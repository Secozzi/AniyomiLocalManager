package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.ui.home.anime.AnimeScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.home.manga.MangaScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesScreenModel

val ViewModelsModule = module {
    viewModelOf(::DataPreferencesScreenModel)
    viewModelOf(::AnimeScreenViewModel)
    viewModelOf(::MangaScreenViewModel)
}
