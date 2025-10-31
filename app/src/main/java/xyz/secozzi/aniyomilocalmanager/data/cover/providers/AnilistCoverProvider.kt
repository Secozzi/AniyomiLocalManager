package xyz.secozzi.aniyomilocalmanager.data.cover.providers

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistCoverData
import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType

class AnilistCoverProvider(
    private val client: HttpClient,
) {
    suspend fun getCoverData(anilistId: Long, type: AnilistSearchType): AnilistCoverData {
        val query = $$"""
            query ($id: Int, $type: MediaType, ) {
                Media (id: $id, type: $type) {
                    coverImage {
                        extraLarge
                        large
                    }
                    idMal
                    startDate {
                        year
                    }
                    title {
                        romaji
                        native
                    }
                    format
                }
            }
        """.trimIndent()

        val data: AnilistResponseDto = client.post(API_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                AnilistRequestBody(
                    query = query,
                    variables = AnilistRequestBody.AnilistRequestBodyVariables(
                        id = anilistId,
                        type = type.name,
                    ),
                ),
            )
        }.body()

        val media = data.data.media

        return AnilistCoverData(
            coverImage = media.coverImage.extraLarge ?: media.coverImage.large,
            publishingYear = media.startDate.year,
            title = media.title.romaji ?: media.title.native,
            format = media.format,
        )
    }

    companion object {
        private const val API_URL = "https://graphql.anilist.co/"
    }
}

@Serializable
private data class AnilistRequestBody(
    val query: String,
    val variables: AnilistRequestBodyVariables,
) {
    @Serializable
    data class AnilistRequestBodyVariables(
        val id: Long,
        val type: String,
    )
}

@Serializable
private data class AnilistResponseDto(
    val data: AnilistDataDto,
) {
    @Serializable
    data class AnilistDataDto(
        @SerialName("Media")
        val media: AnilistMediaDto,
    ) {
        @Serializable
        data class AnilistMediaDto(
            val coverImage: AnilistCoverDto,
            val idMal: Long?,
            val startDate: AnilistStartDateDto,
            val title: AnilistTitleDto,
            val format: String?,
        ) {
            @Serializable
            data class AnilistCoverDto(
                val extraLarge: String?,
                val large: String,
            )

            @Serializable
            data class AnilistStartDateDto(
                val year: Int?,
            )

            @Serializable
            data class AnilistTitleDto(
                val romaji: String?,
                val native: String?,
            )
        }
    }
}
