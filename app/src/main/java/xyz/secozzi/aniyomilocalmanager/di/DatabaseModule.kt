package xyz.secozzi.aniyomilocalmanager.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.Migrations
import xyz.secozzi.aniyomilocalmanager.database.repository.TrackerIdRepositoryImpl
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository

val DatabaseModule = module {
    single<ALMDatabase> {
        Room
            .databaseBuilder(androidContext(), ALMDatabase::class.java, "ALM.db")
            .addMigrations(migrations = Migrations)
            .build()
    }

    singleOf(::TrackerIdRepositoryImpl).bind(TrackerIdRepository::class)
}
