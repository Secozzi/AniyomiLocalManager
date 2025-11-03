package xyz.secozzi.aniyomilocalmanager.di

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
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
                xml(format = get())
                json(json = get())

                // For github raws
                json(
                    json = get(),
                    contentType = ContentType.Text.Plain,
                )
            }
        }
    }
}
