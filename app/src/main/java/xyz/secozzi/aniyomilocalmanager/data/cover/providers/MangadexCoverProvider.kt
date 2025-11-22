package xyz.secozzi.aniyomilocalmanager.data.cover.providers

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData

class MangadexCoverProvider(
    private val context: Context,
    private val client: HttpClient,
) {
    private suspend fun getMangadexId(title: String, year: Int?): String? {
        val data: MangadexResultDto<MangadexSearchResultDto> = client.get(API_URL) {
            url {
                appendPathSegments("manga")
                parameters.append("title", title)
                parameters.append("limit", "1")
                parameters.append("order[relevance]", "desc")
                if (year != null) {
                    parameters.append("year", year.toString())
                }
            }
        }.body()

        return data.data.firstOrNull()?.id
    }

    suspend fun getCovers(title: String, year: Int?): List<CoverData> {
        val mangaId = getMangadexId(title, year) ?: return emptyList()
        val covers = mutableListOf<CoverData>()

        val limit = 100
        var offset = 0
        var total = limit + 1

        while (offset < total) {
            val data: MangadexResultDto<MangadexCoverResultDto> = client.get(API_URL) {
                url {
                    appendPathSegments("cover")
                    parameters.append("order[volume]", "asc")
                    parameters.append("manga[]", mangaId)
                    parameters.append("limit", "100")
                    parameters.append("offset", offset.toString())
                }
            }.body()

            covers.addAll(
                data.data.map {
                    CoverData(
                        origin = context.getString(R.string.pref_mangadex_title),
                        coverUrl = buildString {
                            append(COVER_URL)
                            append("/covers/")
                            append(mangaId)
                            append("/")
                            append(it.attributes.fileName)
                        },
                        hint = it.attributes.volume?.let { vol ->
                            context.getString(R.string.cover_volume_label, vol)
                        },
                    )
                },
            )

            total = data.total
            offset += limit
        }

        return covers
    }

    @Serializable
    data class MangadexResultDto<T>(
        val data: List<T>,
        val total: Int,
    )

    @Serializable
    data class MangadexSearchResultDto(
        val id: String,
    )

    @Serializable
    data class MangadexCoverResultDto(
        val attributes: MangaDexAttributesDto,
    ) {
        @Serializable
        data class MangaDexAttributesDto(
            val fileName: String,
            val volume: String? = null,
        )
    }

    companion object {
        const val API_URL = "https://api.mangadex.org"
        const val COVER_URL = "https://uploads.mangadex.org"
    }
}
