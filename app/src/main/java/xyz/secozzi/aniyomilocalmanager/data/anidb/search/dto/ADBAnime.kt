package xyz.secozzi.aniyomilocalmanager.data.anidb.search.dto

import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchResultItem

class ADBAnime(
    val remoteId: Long,
    val title: String,
    val imageUrl: String,
    val format: String?,
    val startDate: String?,
) : SearchDataItem {
    override fun toSearchItem(): SearchResultItem {
        return SearchResultItem(
            id = remoteId,
            type = format,
            title = title,
            coverUrl = imageUrl,
            startDate = startDate,
            status = null,
            description = null,
        )
    }
}
