package xyz.secozzi.aniyomilocalmanager.data.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ALSearchResult<T>(
    val data: T,
)

@Serializable
data class ALMedia(
    @SerialName("Media")
    val media: ALSearchItem,
)

@Serializable
data class ALSearchPage(
    @SerialName("Page")
    val page: ALSearchMedia,
)

@Serializable
data class ALSearchMedia(
    val media: List<ALSearchItem>,
)
