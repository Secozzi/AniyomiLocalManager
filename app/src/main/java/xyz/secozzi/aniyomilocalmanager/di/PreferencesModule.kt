package xyz.secozzi.aniyomilocalmanager.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.AppearancePreferences
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.AndroidPreferenceStore
import xyz.secozzi.aniyomilocalmanager.preferences.preference.PreferenceStore

val PreferencesModule = module {
    single { AndroidPreferenceStore(androidContext()) }.bind(PreferenceStore::class)

    singleOf(::AniListPreferences)
    singleOf(::AniDBPreferences)
    singleOf(::AppearancePreferences)
    singleOf(::CoverPreferences)
    singleOf(::GeneralPreferences)
}
