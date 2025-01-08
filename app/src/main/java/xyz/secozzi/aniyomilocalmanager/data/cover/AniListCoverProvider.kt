package xyz.secozzi.aniyomilocalmanager.data.cover

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.utils.POST
import xyz.secozzi.aniyomilocalmanager.utils.jsonMime
import xyz.secozzi.aniyomilocalmanager.utils.parseAs

data class AniListCoverData(
    val coverImage: String,
    val malId: Long?,

    // For md
    val publishingYear: Int?,
    val title: String?,

    // For fanart
    val format: String?,
)

class AniListCoverProvider(
    private val client: OkHttpClient,
    private val json: Json,
) {
    fun getCoverData(anilistId: Long, type: AnilistSearchType): AniListCoverData {
        val graphqlQuery = """
            |query (%id: Int, %type: MediaType, ) {
            |	Media (id: %id, type: %type) {
            |		coverImage {
            |			extraLarge
            |			large
            |		}
            |		idMal
            |		startDate {
            |           year
            |       }
            |       title {
            |           romaji
            |           native
            |       }
            |       format
            |	}
            |}
        """.trimMargin().replace("%", "${'$'}")

        val payload = buildJsonObject {
            put("query", graphqlQuery)
            putJsonObject("variables") {
                put("id", anilistId)
                put("type", type.name)
            }
        }

        val data = with(json) {
            client.newCall(
                POST(API_URL, body = payload.toString().toRequestBody(jsonMime)),
            ).execute().parseAs<ALResponseDto>()
                .data.media
        }

        return AniListCoverData(
            malId = data.idMal,
            coverImage = data.coverImage.extraLarge ?: data.coverImage.large,
            publishingYear = data.startDate.year,
            title = data.title.romaji ?: data.title.native,
            format = data.format,
        )
    }

    @Serializable
    data class ALResponseDto(
        val data: ALDataDto,
    ) {
        @Serializable
        data class ALDataDto(
            @SerialName("Media")
            val media: ALMediaDto,
        ) {
            @Serializable
            data class ALMediaDto(
                val coverImage: ALCoverDto,
                val idMal: Long?,
                val startDate: ALStartDateDto,
                val title: ALTitleDto,
                val format: String?,
            ) {
                @Serializable
                data class ALCoverDto(
                    val extraLarge: String?,
                    val large: String,
                )

                @Serializable
                data class ALStartDateDto(
                    val year: Int?,
                )

                @Serializable
                data class ALTitleDto(
                    val romaji: String?,
                    val native: String?,
                )
            }
        }
    }

    companion object {
        private const val API_URL = "https://graphql.anilist.co/"
    }
}
