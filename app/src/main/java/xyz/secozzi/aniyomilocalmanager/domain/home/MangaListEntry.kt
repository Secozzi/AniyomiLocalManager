package xyz.secozzi.aniyomilocalmanager.domain.home

data class MangaListEntry(
    val path: String,
    val name: String,
    val lastModified: String,
    val size: Int,
    val hasCover: Boolean,
    val hasComicInfo: Boolean,
)
