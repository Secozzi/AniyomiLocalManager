package xyz.secozzi.aniyomilocalmanager.di

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import xyz.secozzi.aniyomilocalmanager.BuildConfig
import xyz.secozzi.aniyomilocalmanager.data.ktor.anidb.AniDBHttpClientImpl
import xyz.secozzi.aniyomilocalmanager.domain.ktor.anidb.AniDBHttpClient
import xyz.secozzi.aniyomilocalmanager.domain.ktor.cache.PersistentCache
import xyz.secozzi.aniyomilocalmanager.domain.ktor.ratelimit.RateLimitPlugin
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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

                // For github raws
                json(
                    json = get(),
                    contentType = ContentType.Text.Plain,
                )
            }

            install(HttpCache) {
                publicStorage(PersistentCache(get(), "ktor-cache", 10 * 1024 * 1024))
            }

            install(RateLimitPlugin) {
                addLimit("api.jikan.moe", 3, 1.seconds)
                addLimit("graphql.anilist.co", 90, 1.minutes)
            }
        }
    }

    single<HttpClient>(named("uncached")) {
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

            install(RateLimitPlugin) {
                addLimit("api.anidb.net", 1, 2.seconds)
            }
        }
    }

    single { AniDBHttpClientImpl(get(), get(named("uncached"))) }.bind(AniDBHttpClient::class)
}
