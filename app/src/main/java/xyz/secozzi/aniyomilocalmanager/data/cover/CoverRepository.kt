package xyz.secozzi.aniyomilocalmanager.data.cover

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.anilist.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences

data class CoverData(
    val origin: String,
    val coverUrl: String,
)

class CoverRepository(
    private val context: Context,
    private val preferences: CoverPreferences,
    private val client: OkHttpClient,
    private val json: Json,
) {
    private val aniListCoverProvider by lazy { AniListCoverProvider(client, json) }
    private val malCoverProvider by lazy { MALCoverProvider(client, json) }
    private val mdCoverProvider by lazy { MDCoverProvider(client, json) }
    private val fanartCoverProvider by lazy { FanartCoverProvider(client, json) }

    fun getMangaCovers(anilistId: Long): List<CoverData> {
        val aniListData = aniListCoverProvider.getCoverData(anilistId, AnilistSearchType.MANGA)
        val covers = mutableListOf<CoverData>()

        if (preferences.mangaCoverAnilist.get()) {
            covers.add(
                CoverData(
                    origin = context.getString(R.string.pref_cover_anilist),
                    coverUrl = aniListData.coverImage,
                )
            )
        }

        if (preferences.mangaCoverMAL.get() && aniListData.malId != null) {
            covers.addAll(
                malCoverProvider.getCovers(
                    malId = aniListData.malId,
                    type = MALType.Manga,
                    origin = context.getString(R.string.pref_cover_mal),
                )
            )
        }

        if (preferences.mangaCoverMD.get() && aniListData.title != null) {
            covers.addAll(
                mdCoverProvider.getCovers(
                    title = aniListData.title,
                    year = aniListData.publishingYear,
                    origin = context.getString(R.string.pref_cover_md),
                )
            )
        }

        return covers.distinctBy { it.coverUrl }
    }

    fun getAnimeCovers(anilistId: Long): List<CoverData> {
        val aniListData = aniListCoverProvider.getCoverData(anilistId, AnilistSearchType.ANIME)
        val covers = mutableListOf<CoverData>()

        if (preferences.animeCoverAnilist.get()) {
            covers.add(
                CoverData(
                    origin = context.getString(R.string.pref_cover_anilist),
                    coverUrl = aniListData.coverImage,
                )
            )
        }

        if (preferences.animeCoverMAL.get() && aniListData.malId != null) {
            covers.addAll(
                malCoverProvider.getCovers(
                    malId = aniListData.malId,
                    type = MALType.Anime,
                    origin = context.getString(R.string.pref_cover_mal),
                )
            )
        }

        if (preferences.animeCoverFanart.get() && aniListData.format != null) {
            covers.addAll(
                fanartCoverProvider.getCovers(
                    anilistId = anilistId,
                    type = if (aniListData.format == "MOVIE") "movies" else "tv",
                    origin = context.getString(R.string.pref_cover_fanart),
                )
            )
        }

        return covers
    }
}
