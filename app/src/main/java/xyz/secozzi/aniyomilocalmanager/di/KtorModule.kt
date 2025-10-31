package xyz.secozzi.aniyomilocalmanager.di

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.BuildConfig

val KtorModule = module {
    single<HttpClient> {
        HttpClient(Android) {
            install(UserAgent) {
                agent = buildString {
                    append("Android/")
                    append(Build.VERSION.RELEASE)
                    append(" ")
                    append("AniyomiLocalManager/")
                    append(BuildConfig.VERSION_NAME)
                }
            }

            install(ContentNegotiation) {
                json(json = get())
            }
        }
    }
}
