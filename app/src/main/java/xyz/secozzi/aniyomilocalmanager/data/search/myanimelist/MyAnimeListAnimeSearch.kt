package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListAnimeSearchResult
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListEpisode
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListResult
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.repository.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.preferences.MyAnimeListPreferences

class MyAnimeListAnimeSearch(
    private val client: HttpClient,
    private val myAnimeListPreferences: MyAnimeListPreferences,
) : EpisodeRepository, SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        val data: MyAnimeListResult<List<MyAnimeListAnimeSearchResult>> = client.get(API_URL) {
            url {
                appendPathSegments("anime")
                parameters.append("q", query)
            }
        }.body()

        return data.data.map { a ->
            val titles = a.titles.filter {
                it.type.lowercase() in listOf("default", "synonym", "japanese", "english")
            }
                .sortedBy {
                    when (myAnimeListPreferences.prefLang.get()) {
                        LangPrefEnum.English -> it.type.equals("english", true)
                        LangPrefEnum.Romaji -> it.type.equals("default", true)
                        LangPrefEnum.Native -> it.type.equals("japanese", true)
                    }
                }
                .map { it.title }

            SearchResultItem(
                titles = titles,
                coverUrl = a.images.jpg.let { it.largeImageUrl ?: it.imageUrl ?: it.smallImageUrl },
                type = a.type,
                status = when (a.status?.lowercase()) {
                    "finished airing" -> Status.Completed
                    "currently airing" -> Status.Ongoing
                    else -> Status.Unknown
                },
                description = a.synopsis,
                startDate = a.aired.from?.substringBefore("T"),
                genres = a.genres.map { it.name },
                authors = a.studios.map { it.name },
                artists = emptyList(),
                trackerIds = buildMap {
                    put(TrackerIds.Mal, a.malId.toLong())
                },
            )
        }
    }

    override suspend fun getFromId(id: String): EntryDetails {
        val body: MyAnimeListResult<MyAnimeListAnimeSearchResult> = client.get(API_URL) {
            url {
                appendPathSegments("anime", id)
            }
        }.body()
        val data = body.data

        val titles = data.titles.filter {
            it.type.lowercase() in listOf("default", "synonym", "japanese", "english")
        }
            .sortedBy {
                when (myAnimeListPreferences.prefLang.get()) {
                    LangPrefEnum.English -> it.type.equals("english", true)
                    LangPrefEnum.Romaji -> it.type.equals("default", true)
                    LangPrefEnum.Native -> it.type.equals("japanese", true)
                }
            }
            .map { it.title }

        return EntryDetails(
            title = titles.first(),
            titles = titles.toPersistentList(),
            authors = data.studios.joinToString { it.name },
            artists = "",
            description = data.synopsis.orEmpty(),
            genre = data.genres.joinToString { it.name },
            status = when (data.status?.lowercase()) {
                "finished airing" -> Status.Completed
                "currently airing" -> Status.Ongoing
                else -> Status.Unknown
            },
        )
    }

    override suspend fun getEpisodesFromId(id: String): ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>> {
        val episodes = mutableListOf<EpisodeDetails>()

        var hasNextPage = true
        var page = 1

        while (hasNextPage) {
            val data: MyAnimeListResult<List<MyAnimeListEpisode>> = client.get(API_URL) {
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
                        name = when (myAnimeListPreferences.prefLang.get()) {
                            LangPrefEnum.Native -> ep.titleJapanese ?: ep.titleRomanji ?: ep.title
                            LangPrefEnum.Romaji -> ep.titleRomanji ?: ep.title
                            LangPrefEnum.English -> ep.title
                        },
                        dateUpload = ep.aired?.substringBefore("+"),
                        fillermark = ep.filler,
                        scanlator = null,
                        summary = null,
                        previewUrl = null,
                    )
                },
            )
        }

        return persistentMapOf(EpisodeType.Regular to episodes.toPersistentList())
    }

    companion object {
        private const val API_URL = "https://api.jikan.moe/v4"
    }
}
