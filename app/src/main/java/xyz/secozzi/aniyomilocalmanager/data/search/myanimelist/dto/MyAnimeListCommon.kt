package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
