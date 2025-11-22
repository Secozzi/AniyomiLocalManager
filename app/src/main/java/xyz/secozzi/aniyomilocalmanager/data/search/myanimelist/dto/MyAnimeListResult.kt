package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyAnimeListResult<T>(
    val pagination: MyAnimeListPagination? = null,
    val data: T,
) {
    @Serializable
    data class MyAnimeListPagination(
        @SerialName("has_next_page")
        val hasNextPage: Boolean,
    )
}
