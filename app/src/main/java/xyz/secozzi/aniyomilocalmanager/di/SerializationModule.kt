package xyz.secozzi.aniyomilocalmanager.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val SerializationModule = module {
    single<Json> {
        Json { ignoreUnknownKeys = true }
    }
}
