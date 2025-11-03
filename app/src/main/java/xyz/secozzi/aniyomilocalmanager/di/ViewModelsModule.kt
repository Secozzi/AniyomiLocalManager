package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.ui.anime.cover.AnimeCoverScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.anime.details.AnimeDetailsScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.anime.entry.AnimeEntryScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.home.anime.AnimeScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.home.manga.MangaScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.manga.cover.MangaCoverScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.manga.details.MangaDetailsScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.manga.entry.MangaEntryScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.preferences.DataPreferencesScreenModel
import xyz.secozzi.aniyomilocalmanager.ui.search.SearchScreenViewModel

val ViewModelsModule = module {
    viewModelOf(::DataPreferencesScreenModel)
    viewModelOf(::AnimeScreenViewModel)
    viewModelOf(::MangaScreenViewModel)
    viewModelOf(::AnimeEntryScreenViewModel)
    viewModelOf(::MangaEntryScreenViewModel)
    viewModelOf(::MangaCoverScreenViewModel)
    viewModelOf(::AnimeCoverScreenViewModel)
    viewModelOf(::AnimeDetailsScreenViewModel)
    viewModelOf(::MangaDetailsScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
}
