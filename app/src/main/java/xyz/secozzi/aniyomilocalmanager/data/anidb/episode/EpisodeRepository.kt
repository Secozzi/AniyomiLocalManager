package xyz.secozzi.aniyomilocalmanager.data.anidb.episode

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Response
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto.AniDBAnimeDto
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto.EpisodeModel
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto.toEpisodeModel
import xyz.secozzi.aniyomilocalmanager.utils.GET
import java.util.concurrent.TimeUnit

class EpisodeRepository(
    private val client: OkHttpClient,
    private val xml: XML,
) {
    val anidbClient = client.newBuilder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val cachedRequest = originalRequest.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

            val response = chain.proceed(cachedRequest)

            if (response.code == 504 || isResponseTooOld(response)) {
                response.close()

                val cache = CacheControl.Builder()
                    .maxAge(1, TimeUnit.DAYS)
                    .build()

                val newRequest = originalRequest.newBuilder()
                    .cacheControl(cache)
                    .build()

                return@addInterceptor chain.proceed(newRequest)
            }

            response
        }
        .build()

    private fun isResponseTooOld(response: Response): Boolean {
        val responseTime = response.receivedResponseAtMillis
        val currentTime = System.currentTimeMillis()
        val age = (currentTime - responseTime) / 1000
        return age >= 60 * 60 * 24 // 1 dau
    }

    fun getEpisodes(anidbId: Long): List<EpisodeModel> {
        val resp = anidbClient.newCall(
            GET(URL + anidbId),
        ).execute()

        return xml.decodeFromString<AniDBAnimeDto>(resp.body.string()).episodes.episode.map {
            it.toEpisodeModel()
        }
    }

    companion object {
        private const val CLIENT_NAME = "localfipsklkxaay"
        private const val CLIENT_VER = "1"
        private const val URL = "http://api.anidb.net:9001/httpapi?request=anime&client=$CLIENT_NAME&clientver=$CLIENT_VER&protover=1&aid="
    }
}
