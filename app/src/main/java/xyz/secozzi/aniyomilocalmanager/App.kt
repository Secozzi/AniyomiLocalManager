package xyz.secozzi.aniyomilocalmanager

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import xyz.secozzi.aniyomilocalmanager.di.DatabaseModule
import xyz.secozzi.aniyomilocalmanager.di.PreferencesModule
import xyz.secozzi.aniyomilocalmanager.di.StorageManagerModule
import xyz.secozzi.aniyomilocalmanager.di.ViewModelsModule

@OptIn(KoinExperimentalAPI::class)
class App : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration {
        return koinConfiguration {
            androidContext(this@App)
            modules(
                DatabaseModule,
                PreferencesModule,
                StorageManagerModule,
                ViewModelsModule,
            )
        }
    }
}
