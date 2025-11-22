package xyz.secozzi.aniyomilocalmanager.data.search.myanimelist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import kotlinx.collections.immutable.toPersistentList
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListResult
import xyz.secozzi.aniyomilocalmanager.data.search.myanimelist.dto.MyAnimeListSearchResult
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.mal.model.MyAnimeListSearchType
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.preferences.MyAnimeListPreferences

class MyAnimeListSearch(
    private val client: HttpClient,
    private val myAnimeListPreferences: MyAnimeListPreferences,
) {
    suspend fun search(query: String, type: MyAnimeListSearchType): List<SearchResultItem> {
        val data: MyAnimeListResult<List<MyAnimeListSearchResult>> = client.get(API_URL) {
            url {
                appendPathSegments(
                    when (type) {
                        MyAnimeListSearchType.ANIME -> "anime"
                        MyAnimeListSearchType.MANGA -> "manga"
                    },
                )
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
                    "finished airing", "finished" -> Status.Completed
                    "currently airing", "publishing" -> Status.Ongoing
                    "on hiatus" -> Status.OnHiatus
                    "discontinued" -> Status.Cancelled
                    else -> Status.Unknown
                },
                description = a.synopsis,
                startDate = when (type) {
                    MyAnimeListSearchType.ANIME -> a.aired
                    MyAnimeListSearchType.MANGA -> a.published
                }?.from?.substringBefore("T"),
                genres = a.genres.map { it.name },
                authors = when (type) {
                    MyAnimeListSearchType.ANIME -> a.studios
                    MyAnimeListSearchType.MANGA -> a.authors
                }.orEmpty().map { it.name },
                artists = emptyList(),
                trackerIds = buildMap {
                    put(TrackerIds.Mal, a.malId.toLong())
                },
            )
        }
    }

    suspend fun getFromId(id: Long, type: MyAnimeListSearchType): EntryDetails {
        val body: MyAnimeListResult<MyAnimeListSearchResult> = client.get(API_URL) {
            url {
                appendPathSegments(
                    when (type) {
                        MyAnimeListSearchType.ANIME -> "anime"
                        MyAnimeListSearchType.MANGA -> "manga"
                    },
                    id.toString(),
                )
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
            authors = when (type) {
                MyAnimeListSearchType.ANIME -> data.studios
                MyAnimeListSearchType.MANGA -> data.authors
            }.orEmpty().joinToString { it.name },
            artists = "",
            description = data.synopsis.orEmpty(),
            genre = data.genres.joinToString { it.name },
            status = when (data.status?.lowercase()) {
                "finished airing", "finished" -> Status.Completed
                "currently airing", "publishing" -> Status.Ongoing
                "on hiatus" -> Status.OnHiatus
                "discontinued" -> Status.Cancelled
                else -> Status.Unknown
            },
        )
    }

    companion object {
        const val API_URL = "https://api.jikan.moe/v4"
    }
}
