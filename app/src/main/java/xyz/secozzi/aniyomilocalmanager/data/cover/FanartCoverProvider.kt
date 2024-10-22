package xyz.secozzi.aniyomilocalmanager.data.cover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.utils.GET
import xyz.secozzi.aniyomilocalmanager.utils.parseAs

class FanartCoverProvider(
    private val client: OkHttpClient,
    private val json: Json,
) {
    val mapping by lazy {
        with(json) {
            client.newCall(
                GET(mappingUrl)
            ).execute().parseAs<List<Mapping>>()
        }
    }

    fun getCovers(anilistId: Long, type: String, origin: String): List<CoverData> {
        val tvdbId = mapping.firstOrNull { it.anilistId?.toLong() == anilistId }
            ?.thetvdbId ?: return emptyList()

        val picturesResponse = client.newCall(
            GET("https://webservice.fanart.tv/v3/$type/$tvdbId?api_key=184e1a2b1fe3b94935365411f919f638"),
        ).execute()

        return with(json) {
            picturesResponse.parseAs<FanartDto>().tvposter?.map {
                CoverData(origin, it.url)
            } ?: emptyList()
        }
    }

    @Serializable
    data class FanartDto(
        val tvposter: List<ImageDto>? = null,
    ) {
        @Serializable
        data class ImageDto(
            val url: String,
        )
    }

    companion object {
        private val mappingUrl = "https://raw.githubusercontent.com/Fribb/anime-lists/master/anime-list-mini.json"
    }

    @Serializable
    data class Mapping(
        @SerialName("anilist_id") val anilistId: Int? = null,
        @SerialName("thetvdb_id") val thetvdbId: Int? = null,
    )
}
