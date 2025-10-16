package xyz.secozzi.aniyomilocalmanager.domain.home

data class AnimeListEntry(
    val isSeason: Boolean,
    val path: String,
    val name: String,
    val lastModified: String,
    val size: Int,
    val hasCover: Boolean,
    val hasDetails: Boolean,
    val hasEpisodes: Boolean,
)
