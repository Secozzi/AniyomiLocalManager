package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.data.storage.ArchiveReaderImpl
import xyz.secozzi.aniyomilocalmanager.data.storage.StorageManagerImpl
import xyz.secozzi.aniyomilocalmanager.data.storage.ZipWriterImpl
import xyz.secozzi.aniyomilocalmanager.domain.storage.ArchiveReader
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.domain.storage.ZipWriter
import xyz.secozzi.aniyomilocalmanager.utils.FilesComparator

val StorageManagerModule = module {
    singleOf(::StorageManagerImpl).bind(StorageManager::class)
    factoryOf(::ArchiveReaderImpl).bind(ArchiveReader::class)
    factoryOf(::ZipWriterImpl).bind(ZipWriter::class)
    singleOf(::FilesComparator)
}
