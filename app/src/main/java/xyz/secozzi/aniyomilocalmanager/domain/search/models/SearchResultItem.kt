package xyz.secozzi.aniyomilocalmanager.domain.search.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds

@Serializable
@Immutable
data class SearchResultItem(
    val titles: List<String>,
    val coverUrl: String?,
    val type: String?,
    val status: Status,
    val description: String?,
    val startDate: String?,
    val genres: List<String>,
    val authors: List<String>,
    val artists: List<String>,
    val trackerIds: Map<TrackerIds, Long>,
)
