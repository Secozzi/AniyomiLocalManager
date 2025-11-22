package xyz.secozzi.aniyomilocalmanager.data.search.mangabaka

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultDto(
    val data: List<SearchResultDto>,
)

@Serializable
data class SingleResultDto(
    val data: SearchResultDto,
)

@Serializable
data class SearchResultDto(
    val id: Long,
    val title: String,
    @SerialName("native_title")
    val nativeTitle: String? = null,
    @SerialName("romanized_title")
    val romanizedTitle: String? = null,
    val cover: CoverDto,
    val authors: List<String>? = null,
    val artists: List<String>? = null,
    val genres: List<String>? = null,
    val description: String? = null,
    val type: String,
    val status: String,
    val year: Int? = null,
    val source: SourceDto,
)

@Serializable
data class CoverDto(
    val raw: RawCoverDto,
) {
    @Serializable
    data class RawCoverDto(
        val url: String? = null,
    )
}

@Serializable
data class SourceDto(
    val anilist: IdDto<Long>,
    @SerialName("my_anime_list")
    val mal: IdDto<Long>,
)

@Serializable
data class IdDto<T>(
    val id: T? = null,
)
