package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListEpisode
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListResult
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.repository.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.mal.model.MyAnimeListSearchType
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.preferences.MyAnimeListPreferences

class MyAnimeListAnimeSearch(
    private val client: HttpClient,
    private val myAnimeListPreferences: MyAnimeListPreferences,
    private val myAnimeListSearch: MyAnimeListSearch,
) : EpisodeRepository, SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        return myAnimeListSearch.search(query, MyAnimeListSearchType.ANIME)
    }

    override suspend fun getFromId(id: String): EntryDetails {
        val id = id.toLongOrNull() ?: return EntryDetails.Empty
        return myAnimeListSearch.getFromId(id, MyAnimeListSearchType.ANIME)
    }

    override suspend fun getEpisodesFromId(id: String): ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>> {
        val episodes = mutableListOf<EpisodeDetails>()

        var hasNextPage = true
        var page = 1

        while (hasNextPage) {
            val data: MyAnimeListResult<List<MyAnimeListEpisode>> = client.get(MyAnimeListSearch.API_URL) {
                url {
                    appendPathSegments("anime", id, "episodes")
                    parameters.append("page", page.toString())
                }
            }.body()

            page++
            hasNextPage = data.pagination?.hasNextPage == true

            episodes.addAll(
                data.data.map { ep ->
                    EpisodeDetails(
                        episodeNumber = ep.malId.toFloat(),
                        name = getEpisodeFromTemplate(ep, myAnimeListPreferences.nameFormat.get()),
                        dateUpload = ep.aired?.substringBefore("+"),
                        fillermark = ep.filler,
                        scanlator = getEpisodeFromTemplate(ep, myAnimeListPreferences.scanlatorFormat.get()),
                        summary = getEpisodeFromTemplate(ep, myAnimeListPreferences.summaryFormat.get()),
                        previewUrl = null,
                    )
                },
            )
        }

        return persistentMapOf(EpisodeType.Regular to episodes.toPersistentList())
    }

    private fun getEpisodeFromTemplate(episode: MyAnimeListEpisode, template: String): String? {
        return template
            .replace("%eng", episode.title)
            .replace("%rom", episode.titleRomanji ?: "")
            .replace("%nat", episode.titleJapanese ?: "")
            .replace("%ep", episode.malId.toString())
            .replace("%air", episode.aired?.substringBefore("+") ?: "")
            .replace("%sco", episode.score?.toString() ?: "")
            .replace("%fil", episode.filler.toString())
            .replace("%rec", episode.recap.toString())
            .takeIf { it.isNotBlank() }
    }
}
