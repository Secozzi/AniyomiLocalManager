package xyz.secozzi.aniyomilocalmanager.data.cover

import android.content.Context
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.AnilistCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.FanartCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.MalCoverProvider
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.MalType
import xyz.secozzi.aniyomilocalmanager.data.cover.providers.MangadexCoverProvider
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity
import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.CoverRepository

class CoverRepositoryImpl(
    private val context: Context,
    private val anilistCoverProvider: AnilistCoverProvider,
    private val malCoverProvider: MalCoverProvider,
    private val mangadexCoverProvider: MangadexCoverProvider,
    private val fanartCoverProvider: FanartCoverProvider,
) : CoverRepository {
    override suspend fun getMangaCovers(
        trackerEntity: MangaTrackerEntity,
        anilist: Boolean,
        mal: Boolean,
        mangadex: Boolean,
    ): List<CoverData> {
        val covers = mutableListOf<CoverData>()
        val anilistData = if (anilist || mangadex) {
            trackerEntity.anilist?.let {
                anilistCoverProvider.getCoverData(it, AnilistSearchType.MANGA)
            }
        } else {
            null
        }

        val titleYear = when {
            !mangadex -> null
            anilistData != null && anilistData.title != null -> anilistData.title to anilistData.publishingYear
            trackerEntity.mal != null -> malCoverProvider.getTitleAndYear(trackerEntity.mal)
            else -> null
        }

        if (anilist && anilistData != null) {
            covers.add(
                CoverData(
                    origin = context.getString(R.string.pref_anilist_title),
                    coverUrl = anilistData.coverImage,
                    hint = null,
                ),
            )
        }

        if (mal && trackerEntity.mal != null) {
            covers.addAll(
                malCoverProvider.getCovers(
                    malId = trackerEntity.mal,
                    type = MalType.Manga,
                ),
            )
        }

        if (mangadex && titleYear != null) {
            covers.addAll(
                mangadexCoverProvider.getCovers(titleYear.first, titleYear.second),
            )
        }

        return covers.distinctBy { it.coverUrl }
    }

    override suspend fun getAnimeCovers(
        trackerEntity: AnimeTrackerEntity,
        anilist: Boolean,
        mal: Boolean,
        fanart: Boolean,
    ): List<CoverData> {
        val covers = mutableListOf<CoverData>()

        if (anilist && trackerEntity.anilist != null) {
            anilistCoverProvider.getCoverData(trackerEntity.anilist, AnilistSearchType.ANIME).let {
                covers.add(
                    CoverData(
                        origin = context.getString(R.string.pref_anilist_title),
                        coverUrl = it.coverImage,
                        hint = null,
                    ),
                )
            }
        }

        if (mal && trackerEntity.mal != null) {
            covers.addAll(
                malCoverProvider.getCovers(
                    malId = trackerEntity.mal,
                    type = MalType.Anime,
                ),
            )
        }

        if (fanart) {
            covers.addAll(
                fanartCoverProvider.getCovers(trackerEntity.anilist, trackerEntity.mal, trackerEntity.anidb),
            )
        }

        return covers.distinctBy { it.coverUrl }
    }
}
