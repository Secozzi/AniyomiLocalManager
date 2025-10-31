package xyz.secozzi.aniyomilocalmanager.data.search.mangabaka

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
                title = when (mangaBakaPreferences.prefLang.get()) {
                    LangPrefEnum.English -> it.title
                    LangPrefEnum.Romaji -> it.romanizedTitle ?: it.title
                    LangPrefEnum.Native -> it.nativeTitle ?: it.romanizedTitle ?: it.title
                },
                coverUrl = it.cover.raw,
                type = it.type,
                status = it.status,
                description = it.description,
                startDate = it.year?.toString(),
                trackerIds = buildMap {
                    put(TrackerIds.MangaBaka, it.id)
                    it.source.mal.id?.let { id ->
                        put(TrackerIds.Mal, id)
                    }
                    it.source.anilist.id?.let { id ->
                        put(TrackerIds.AniList, id)
                    }
                },
            )
        }
    }
}
