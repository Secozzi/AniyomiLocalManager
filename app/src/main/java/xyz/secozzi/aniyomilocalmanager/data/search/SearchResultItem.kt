package xyz.secozzi.aniyomilocalmanager.data.search

data class SearchResultItem(
    val id: Long,
    val type: String?,
    val title: String,
    val coverUrl: String,
    val status: String?,
    val description: String?,
    val startDate: String?,
)
