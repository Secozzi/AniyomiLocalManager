package xyz.secozzi.aniyomilocalmanager.data.anilist.dto

import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.model.Status

class ALAnime(
    val remoteId: Long,
    val titles: List<String>,
    val imageUrl: String,
    val description: String?,
    val format: String?,
    val publishingStatus: Status,
    val startDate: String?,
    val genre: String,
    val studio: String,
) : SearchDataItem {
    override fun toSearchItem(): SearchResultItem {
        return SearchResultItem(
            id = remoteId,
            type = format,
            title = titles.first(),
            coverUrl = imageUrl,
            status = publishingStatus.displayName,
            description = description,
            startDate = startDate,
        )
    }
}
