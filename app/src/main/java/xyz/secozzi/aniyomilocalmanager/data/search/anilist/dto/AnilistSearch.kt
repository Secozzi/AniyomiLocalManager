package xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnilistSearchResult<T>(
    val data: T,
)

@Serializable
data class AnilistMedia(
    @SerialName("Media")
    val media: AnilistSearchItem,
)

@Serializable
data class AnilistSearchPage(
    @SerialName("Page")
    val page: AnilistSearchMedia,
)

@Serializable
data class AnilistSearchMedia(
    val media: List<AnilistSearchItem>,
)
