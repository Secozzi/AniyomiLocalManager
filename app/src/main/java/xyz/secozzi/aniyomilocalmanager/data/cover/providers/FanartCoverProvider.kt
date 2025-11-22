package xyz.secozzi.aniyomilocalmanager.data.cover.providers

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.MappingRepository

class FanartCoverProvider(
    private val context: Context,
    private val client: HttpClient,
    private val mappingRepository: MappingRepository,
) {
    suspend fun getCovers(anilistId: Long?, malId: Long?, anidbId: Long?): List<CoverData> {
        val (type, id) = mappingRepository.getTvdbId(anilistId, malId, anidbId) ?: return emptyList()
        val tvdbType = if (type == "MOVIE") "movies" else "tv"

        val data: FanartDto = client.get("https://webservice.fanart.tv/v3") {
            url {
                appendPathSegments(tvdbType, id.toString())
                parameters.append("api_key", "184e1a2b1fe3b94935365411f919f638")
            }
        }.body()

        return data.tvposter?.map {
            CoverData(
                origin = context.getString(R.string.pref_fanart_title),
                coverUrl = it.url,
                hint = null,
            )
        } ?: emptyList()
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
}
