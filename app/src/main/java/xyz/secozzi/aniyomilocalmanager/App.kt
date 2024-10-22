package xyz.secozzi.aniyomilocalmanager

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.secozzi.aniyomilocalmanager.di.FileManagerModule
import xyz.secozzi.aniyomilocalmanager.di.NetworkModule
import xyz.secozzi.aniyomilocalmanager.di.PreferencesModule
import xyz.secozzi.aniyomilocalmanager.di.RepositoryModule
import xyz.secozzi.aniyomilocalmanager.di.SerializationModule
import xyz.secozzi.aniyomilocalmanager.presentation.crash.CrashActivity
import xyz.secozzi.aniyomilocalmanager.presentation.crash.GlobalExceptionHandler

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(applicationContext, CrashActivity::class.java)
        )

        startKoin {
            androidContext(this@App)

            modules(
                FileManagerModule,
                NetworkModule,
                PreferencesModule,
                RepositoryModule,
                SerializationModule,
            )
        }
    }
}
