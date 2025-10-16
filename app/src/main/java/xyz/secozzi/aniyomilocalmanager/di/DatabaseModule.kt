package xyz.secozzi.aniyomilocalmanager.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.data.AnimeTrackerRepositoryImpl
import xyz.secozzi.aniyomilocalmanager.database.data.MangaTrackerRepositoryImpl
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository

val DatabaseModule = module {
    single<ALMDatabase> {
        Room
            .databaseBuilder(androidContext(), ALMDatabase::class.java, "ALM.db")
            .build()
    }

    singleOf(::AnimeTrackerRepositoryImpl).bind(AnimeTrackerRepository::class)
    singleOf(::MangaTrackerRepositoryImpl).bind(MangaTrackerRepository::class)
}
