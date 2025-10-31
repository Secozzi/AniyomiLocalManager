package xyz.secozzi.aniyomilocalmanager.domain.anilist.model

data class AnilistCoverData(
    val coverImage: String,

    // For mangadex
    val publishingYear: Int?,
    val title: String?,

    // For fanart
    val format: String?,
)
