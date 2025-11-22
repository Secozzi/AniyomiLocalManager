package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MyAnimeListSearchResult(
    @SerialName("mal_id")
    val malId: Int,
    val images: MyAnimeListImage,
    val titles: List<MyAnimeListTitle>,
    val type: String? = null,
    val status: String? = null,
    val published: MyAnimeListDate? = null,
    val aired: MyAnimeListDate? = null,
    val authors: List<MyAnimeListCreator>? = null,
    val studios: List<MyAnimeListCreator>? = null,
    val synopsis: String? = null,
    val genres: List<MyAnimeListGenre>,
)

@Serializable
data class MyAnimeListTitle(
    val type: String,
    val title: String,
)

@Serializable
data class MyAnimeListImage(
    val jpg: MyAnimeListJpg,
) {
    @Serializable
    data class MyAnimeListJpg(
        @SerialName("image_url")
        val imageUrl: String? = null,
        @SerialName("small_image_url")
        val smallImageUrl: String? = null,
        @SerialName("large_image_url")
        val largeImageUrl: String? = null,
    )
}

@Serializable
data class MyAnimeListDate(
    val from: String? = null,
)

@Serializable
data class MyAnimeListCreator(
    val name: String,
)

@Serializable
data class MyAnimeListGenre(
    val name: String,
)
