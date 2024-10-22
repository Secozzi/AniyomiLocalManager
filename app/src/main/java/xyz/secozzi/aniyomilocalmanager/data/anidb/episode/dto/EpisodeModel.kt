package xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto

import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType

data class EpisodeModel(
    val englishTitle: String?,
    val romajiTitle: String?,
    val nativeTitle: String?,
    val episodeNumber: Int,
    val duration: Int?,
    val airingDate: String?,
    val rating: String?,
    val summary: String?,
    val episodeType: EpisodeType,
)
