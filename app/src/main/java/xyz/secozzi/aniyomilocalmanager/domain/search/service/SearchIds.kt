package xyz.secozzi.aniyomilocalmanager.domain.search.service

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R

@Serializable
enum class SearchIds(@param:StringRes val stringRes: Int) {
    MangaBaka(R.string.pref_mangabaka_title),
    AnilistAnime(R.string.pref_anilist_title),
    AnilistManga(R.string.pref_anilist_title),
    MalAnime(R.string.pref_mal_title),
    MalManga(R.string.pref_mal_title),
}
