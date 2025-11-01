package xyz.secozzi.aniyomilocalmanager.data.search.mangabaka

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.preferences.MangaBakaPreferences

class MangaBakaSearch(
    private val client: HttpClient,
    private val mangaBakaPreferences: MangaBakaPreferences,
) : SearchRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        val result: ResultDto = client.get("https://api.mangabaka.dev/v1/series/search") {
            url {
                parameters.append("q", query)
            }
        }.body()

        return result.data.map {
            SearchResultItem(
                titles = when (mangaBakaPreferences.prefLang.get()) {
                    LangPrefEnum.English -> listOfNotNull(it.title, it.romanizedTitle, it.nativeTitle)
                    LangPrefEnum.Romaji -> listOfNotNull(it.romanizedTitle, it.title, it.nativeTitle)
                    LangPrefEnum.Native -> listOfNotNull(it.nativeTitle, it.romanizedTitle, it.title)
                },
                coverUrl = it.cover.raw,
                type = it.type,
                status = when (it.status) {
                    "cancelled" -> Status.Cancelled
                    "completed" -> Status.Completed
                    "hiatus" -> Status.OnHiatus
                    "releasing" -> Status.Ongoing
                    "unknown" -> Status.Unknown
                    else -> Status.Unknown
                },
                description = it.description,
                startDate = it.year?.toString(),
                authors = it.authors.orEmpty(),
                artists = it.artists.orEmpty(),
                genres = it.genres.orEmpty(),
                trackerIds = buildMap {
                    put(TrackerIds.MangaBaka, it.id)
                    it.source.mal.id?.let { id ->
                        put(TrackerIds.Mal, id)
                    }
                    it.source.anilist.id?.let { id ->
                        put(TrackerIds.Anilist, id)
                    }
                },
            )
        }
    }
}
