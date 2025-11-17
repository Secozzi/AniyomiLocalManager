package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyAnimeListEpisode(
    @SerialName("mal_id")
    val malId: Int,
    val title: String,
    @SerialName("title_japanese")
    val titleJapanese: String? = null,
    @SerialName("title_romanji")
    val titleRomanji: String? = null,
    val aired: String? = null,
    val score: Float? = null,
    val filler: Boolean,
)
