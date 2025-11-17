package xyz.secozzi.aniyomilocalmanager.data.ktor.anidb

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.expires
import io.ktor.http.takeFrom
import io.ktor.util.date.GMTDate
import xyz.secozzi.aniyomilocalmanager.domain.ktor.anidb.AniDBHttpClient
import xyz.secozzi.aniyomilocalmanager.domain.ktor.cache.PersistentCache

class AniDBHttpClientImpl(
    context: Context,
    private val client: HttpClient,
) : AniDBHttpClient {
    private val cache = PersistentCache(context, "anidb-cache", 50 * 1024 * 1024)

    override suspend fun get(urlString: String, block: HttpRequestBuilder.() -> Unit): ByteArray {
        val time = System.currentTimeMillis()

        val finalUrl = HttpRequestBuilder().apply {
            url.takeFrom(urlString)
            block()
        }.url.build()

        cache.findAll(finalUrl).firstOrNull()?.let {
            // Use cache if < 1 day, 5 min old
            if ((time - it.requestTime.timestamp) < 1000 * 60 * 60 * 24 + 1000 * 60 * 5) {
                return it.body
            }
        }

        val response = client.get(urlString, block)
        val responseBody = response.bodyAsBytes()
        val cached = CachedResponseData(
            url = finalUrl,
            statusCode = response.status,
            requestTime = response.requestTime,
            responseTime = response.responseTime,
            version = response.version,
            expires = GMTDate(response.expires()?.time),
            headers = response.headers,
            varyKeys = emptyMap(),
            body = responseBody,
        )

        cache.store(finalUrl, cached)
        return responseBody
    }
}
