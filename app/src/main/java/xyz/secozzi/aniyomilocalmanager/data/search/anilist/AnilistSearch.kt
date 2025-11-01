package xyz.secozzi.aniyomilocalmanager.data.search.anilist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto.AnilistSearchPage
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto.AnilistSearchResult
import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.preferences.AnilistPreferences

class AnilistSearch(
    private val client: HttpClient,
    private val anilistPreferences: AnilistPreferences,
) {
    suspend fun search(searchQuery: String, type: AnilistSearchType): List<SearchResultItem> {
        val query = $$"""
            query Search($query: String, $type: MediaType) {
                Page (perPage: 50) {
                    media(search: $query, type: $type) {
                        id
                        idMal
                        title {
                            native
                            romaji
                            english
                        }
                        coverImage {
                            extraLarge
                            large
                        }
                        format
                        status
                        description
                        startDate {
                            year
                            month
                            day
                        }
                        genres
                        studios {
                            edges {
                                node {
                                    name
                                }
                            }
                        }
                        staff(perPage: 5) {
                            edges {
                                node {
                                    name {
                                        full
                                    }
                                }
                                role
                            }
                        }
                    }
                }
            }
        """.trimIndent()

        val data: AnilistSearchResult<AnilistSearchPage> = client.post(API_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                AnilistRequestBody(
                    query = query,
                    variables = AnilistRequestBody.AnilistRequestBodyVariables(
                        query = searchQuery,
                        type = type.name,
                    ),
                ),
            )
        }.body()

        val media = data.data.page.media

        return media.map {
            it.toSearchResultItem(
                titlePreference = anilistPreferences.prefLang.get(),
                studioCountPreference = anilistPreferences.studioCount.get(),
                isManga = type == AnilistSearchType.MANGA,
            )
        }
    }

    @Serializable
    data class AnilistRequestBody(
        val query: String,
        val variables: AnilistRequestBodyVariables,
    ) {
        @Serializable
        data class AnilistRequestBodyVariables(
            val query: String,
            val type: String,
        )
    }

    companion object {
        private const val API_URL = "https://graphql.anilist.co/"
    }
}
