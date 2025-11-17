package xyz.secozzi.aniyomilocalmanager.domain.ktor.anidb

import io.ktor.client.request.HttpRequestBuilder

interface AniDBHttpClient {
    suspend fun get(urlString: String, block: HttpRequestBuilder.() -> Unit = {}): ByteArray
}
