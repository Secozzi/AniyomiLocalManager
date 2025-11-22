package xyz.secozzi.aniyomilocalmanager.data.search.anilist

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto.AnilistMedia
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto.AnilistSearchPage
import xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto.AnilistSearchResult
import xyz.secozzi.aniyomilocalmanager.domain.anilist.model.AnilistSearchType
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
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
                    variables = AnilistSearchRequestBodyVariables(
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

    suspend fun getFromId(id: Long, type: AnilistSearchType): EntryDetails {
        val query = $$"""
            query media($id: Int, $type: MediaType) {
                Media(id: $id, type: $type) {
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
        """.trimIndent()

        val data: AnilistSearchResult<AnilistMedia> = client.post(API_URL) {
            contentType(ContentType.Application.Json)
            setBody(
                AnilistRequestBody(
                    query = query,
                    variables = AnilistIdRequestBodyVariables(
                        id = id,
                        type = type.name,
                    ),
                ),
            )
        }.body()

        val searchResultItem = data.data.media.toSearchResultItem(
            titlePreference = anilistPreferences.prefLang.get(),
            studioCountPreference = anilistPreferences.studioCount.get(),
            isManga = type == AnilistSearchType.MANGA,
        )

        return EntryDetails(
            title = searchResultItem.titles.firstOrNull().orEmpty(),
            titles = searchResultItem.titles.toPersistentList(),
            authors = searchResultItem.authors.joinToString(),
            artists = searchResultItem.artists.joinToString(),
            description = searchResultItem.description.orEmpty(),
            genre = searchResultItem.genres.joinToString(),
            status = searchResultItem.status,
        )
    }

    @Serializable
    data class AnilistRequestBody<T>(
        val query: String,
        val variables: T,
    )

    @Serializable
    data class AnilistSearchRequestBodyVariables(
        val query: String,
        val type: String,
    )

    @Serializable
    data class AnilistIdRequestBodyVariables(
        val id: Long,
        val type: String,
    )

    companion object {
        private const val API_URL = "https://graphql.anilist.co/"
    }
}
