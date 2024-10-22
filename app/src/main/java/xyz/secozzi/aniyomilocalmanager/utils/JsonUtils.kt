package xyz.secozzi.aniyomilocalmanager.utils

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.serializer
import okhttp3.Response

context(Json)
inline fun <reified T> Response.parseAs(): T {
    return decodeFromJsonResponse(serializer(), this)
}

context(Json)
fun <T> decodeFromJsonResponse(
    deserializer: DeserializationStrategy<T>,
    response: Response,
): T {
    return response.body.source().use {
        decodeFromBufferedSource(deserializer, it)
    }
}
