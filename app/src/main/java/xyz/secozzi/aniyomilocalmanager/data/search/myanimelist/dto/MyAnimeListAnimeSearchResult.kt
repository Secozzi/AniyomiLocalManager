package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyAnimeListAnimeSearchResult(
    @SerialName("mal_id")
    val malId: Int,
    val images: MyAnimeListImage,
    val title: String,
    val titles: List<MyAnimeListTitle>,
    val type: String? = null,
    val status: String? = null,
    val aired: MyAnimeListAired,
    val synopsis: String? = null,
    val studios: List<MyAnimeListStudio>,
    val genres: List<MyAnimeListGenre>,
)

@Serializable
data class MyAnimeListAired(
    val from: String? = null,
)

@Serializable
data class MyAnimeListStudio(
    val name: String,
)

@Serializable
data class MyAnimeListGenre(
    val name: String,
)
