package xyz.secozzi.aniyomilocalmanager.data.cover

import android.os.Build
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.BuildConfig
import xyz.secozzi.aniyomilocalmanager.utils.GET
import xyz.secozzi.aniyomilocalmanager.utils.parseAs

class MDCoverProvider(
    private val client: OkHttpClient,
    private val json: Json,
) {
    fun getMDId(title: String, year: Int?): String? {
        val searchUrl = API_URL.toHttpUrl().newBuilder().apply {
            addPathSegment("manga")
            addQueryParameter("title", title)
            addQueryParameter("limit", "1")
            addQueryParameter("order[relevance]", "desc")
            if (year != null) {
                addQueryParameter("year", year.toString())
            }
        }.build().toString()

        val data = with(json) {
            client.newCall(
                GET(searchUrl, headers = Headers.headersOf("User-Agent", userAgent))
            ).execute().parseAs<MDResultDto<MDSearchResultDto>>()
        }

        return data.data.firstOrNull()?.id
    }

    fun getCovers(title: String, year: Int?, origin: String): List<CoverData> {
         val mangaId = getMDId(title, year) ?: return emptyList()

        val coversUrl = API_URL.toHttpUrl().newBuilder().apply {
            addPathSegment("cover")
            addQueryParameter("order[volume]", "asc")
            addQueryParameter("manga[]", mangaId)
            addQueryParameter("limit", "100")
        }.build().toString()

        val data = with(json) {
            client.newCall(
                GET(coversUrl, headers = Headers.headersOf("User-Agent", userAgent))
            ).execute().parseAs<MDResultDto<MDCoverResultDto>>()
        }

        return data.data.map { coverId ->
            CoverData(
                origin = origin,
                coverUrl = buildString {
                    append(COVER_URL)
                    append("/covers/")
                    append(mangaId)
                    append("/")
                    append(coverId.attributes.fileName)
                }
            )
        }
    }

    @Serializable
    data class MDResultDto<T>(
        val data: List<T>
    )

    @Serializable
    data class MDSearchResultDto(
        val id: String,
    )

    @Serializable
    data class MDCoverResultDto(
        val attributes: MDAttrDto,
    ) {
        @Serializable
        data class MDAttrDto(
            val fileName: String,
        )
    }

    companion object {
        const val API_URL = "https://api.mangadex.org"
        const val COVER_URL = "https://uploads.mangadex.org"

        val userAgent = buildString {
            append("Android/")
            append(Build.VERSION.RELEASE)
            append(" ")
            append("AniyomiLocalManager/")
            append(BuildConfig.VERSION_NAME)
        }
    }
}
