package xyz.secozzi.aniyomilocalmanager.domain.search.models

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultItem(
    val title: String,
    val coverUrl: String?,
    val type: String?,
    val status: String?,
    val description: String?,
    val startDate: String?,
    val trackerIds: Map<Long, Long>,
)
