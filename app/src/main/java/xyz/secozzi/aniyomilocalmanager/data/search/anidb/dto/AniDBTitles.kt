package xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto

data class AniDBTitleAnime(
    val aid: String,
    val title: List<AniDBTitleAnimeTitle>,
)

data class AniDBTitleAnimeTitle(
    val language: String,
    val value: String,
)
