package xyz.secozzi.aniyomilocalmanager.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverRepositoryImpl
import xyz.secozzi.aniyomilocalmanager.data.cover.MappingRepositoryImpl
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.AnilistCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.FanartCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.MalCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.MangadexCoverProvider
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.CoverRepository
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.MappingRepository

val CoverModule = module {
    singleOf(::MappingRepositoryImpl).bind(MappingRepository::class)
    singleOf(::MalCoverProvider)
    singleOf(::MangadexCoverProvider)
    singleOf(::AnilistCoverProvider)
    singleOf(::FanartCoverProvider)
    singleOf(::CoverRepositoryImpl).bind(CoverRepository::class)
}
