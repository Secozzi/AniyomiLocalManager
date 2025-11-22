package xyz.secozzi.aniyomilocalmanager

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import xyz.secozzi.aniyomilocalmanager.di.CoverModule
import xyz.secozzi.aniyomilocalmanager.di.DatabaseModule
import xyz.secozzi.aniyomilocalmanager.di.KtorModule
import xyz.secozzi.aniyomilocalmanager.di.PreferencesModule
import xyz.secozzi.aniyomilocalmanager.di.SearchModule
import xyz.secozzi.aniyomilocalmanager.di.SerializationModule
import xyz.secozzi.aniyomilocalmanager.di.StorageManagerModule
import xyz.secozzi.aniyomilocalmanager.di.ViewModelsModule
import xyz.secozzi.aniyomilocalmanager.ui.crash.CrashActivity
import xyz.secozzi.aniyomilocalmanager.ui.crash.GlobalExceptionHandler

@OptIn(KoinExperimentalAPI::class)
class App : Application(), KoinStartup {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(applicationContext, CrashActivity::class.java),
        )
    }

    override fun onKoinStartup(): KoinConfiguration {
        return koinConfiguration {
            androidLogger()
            androidContext(this@App)
            modules(
                SerializationModule,
                KtorModule,
                DatabaseModule,
                PreferencesModule,
                CoverModule,
                StorageManagerModule,
                SearchModule,
                ViewModelsModule,
            )
        }
    }
}
