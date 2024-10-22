package xyz.secozzi.aniyomilocalmanager.data.cover

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.utils.GET
import xyz.secozzi.aniyomilocalmanager.utils.parseAs

enum class MALType(val type: String) {
    Anime("anime"),
    Manga("manga"),
}

class MALCoverProvider(
    private val client: OkHttpClient,
    private val json: Json,
) {
    fun getCovers(malId: Long, type: MALType, origin: String): List<CoverData> {
        val picturesResponse = with(json) {
            client.newCall(
                GET("https://api.jikan.moe/v4/${type.type}/$malId/pictures"),
            ).execute().parseAs<MALPicturesDto>()
        }

        return picturesResponse.data.mapNotNull { imgs ->
            imgs.jpg.let { it.largeImageUrl ?: it.imageUrl ?: it.smallImageUrl }
        }.map { CoverData(origin, it) }
    }

    @Serializable
    data class MALPicturesDto(
        val data: List<MALCoverDto>,
    ) {
        @Serializable
        data class MALCoverDto(
            val jpg: MALJpgDto,
        ) {
            @Serializable
            data class MALJpgDto(
                @SerialName("image_url") val imageUrl: String? = null,
                @SerialName("small_image_url") val smallImageUrl: String? = null,
                @SerialName("large_image_url") val largeImageUrl: String? = null,
            )
        }
    }
}
