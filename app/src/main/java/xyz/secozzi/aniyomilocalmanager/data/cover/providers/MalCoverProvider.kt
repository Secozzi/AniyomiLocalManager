package xyz.secozzi.aniyomilocalmanager.data.cover.providers

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData

enum class MalType(val type: String) {
    Anime("anime"),
    Manga("manga"),
}

class MalCoverProvider(
    private val context: Context,
    private val client: HttpClient,
) {
    suspend fun getCovers(malId: Long, type: MalType): List<CoverData> {
        val picturesResponse: MalResultDto<List<MalCoverDto>> = client
            .get("https://api.jikan.moe/v4/${type.type}/$malId/pictures")
            .body()

        return picturesResponse.data.mapNotNull { imgs ->
            imgs.jpg.let { it.largeImageUrl ?: it.imageUrl ?: it.smallImageUrl }
        }.map {
            CoverData(
                origin = context.getString(R.string.pref_mal_title),
                coverUrl = it,
                hint = null,
            )
        }
    }

    suspend fun getTitleAndYear(malId: Long): Pair<String, Int?> {
        val mangaResponse: MalResultDto<MalMangaDto> = client
            .get("https://api.jikan.moe/v4/manga/$malId")
            .body()

        return mangaResponse.data.title to mangaResponse.data.published.prop.from.year
    }

    @Serializable
    data class MalResultDto<T>(
        val data: T,
    )

    @Serializable
    data class MalMangaDto(
        val title: String,
        val published: MalMangaPublishedDto,
    ) {
        @Serializable
        data class MalMangaPublishedDto(
            val prop: MalMangaPublishedPropDto,
        ) {
            @Serializable
            data class MalMangaPublishedPropDto(
                val from: MalMangaPublishedFromDto,
            ) {
                @Serializable
                data class MalMangaPublishedFromDto(
                    val year: Int? = null,
                )
            }
        }
    }

    @Serializable
    data class MalCoverDto(
        val jpg: MalJpgDto,
    ) {
        @Serializable
        data class MalJpgDto(
            @SerialName("image_url") val imageUrl: String? = null,
            @SerialName("small_image_url") val smallImageUrl: String? = null,
            @SerialName("large_image_url") val largeImageUrl: String? = null,
        )
    }
}
