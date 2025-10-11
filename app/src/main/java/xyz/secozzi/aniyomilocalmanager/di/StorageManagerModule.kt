package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.data.storage.StorageManagerImpl
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator

val StorageManagerModule = module {
    singleOf(::StorageManagerImpl).bind(StorageManager::class)
    singleOf(::FilesComparator)
}
