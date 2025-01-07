package xyz.secozzi.aniyomilocalmanager.data.anilist

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALMedia
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALSearchItem
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALSearchPage
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALSearchResult
import xyz.secozzi.aniyomilocalmanager.utils.jsonMime
import xyz.secozzi.aniyomilocalmanager.utils.parseAs
import xyz.secozzi.aniyomilocalmanager.utils.POST

enum class AnilistSearchType {
    ANIME,
    MANGA,
}

class AnilistSearch(
    private val client: OkHttpClient,
    private val json: Json,
) {
    suspend fun search(query: String, type: AnilistSearchType): List<ALSearchItem> {
        val QUERY = """
            |query Search(%query: String, %type: MediaType) {
            |    Page (perPage: 50) {
            |        media(search: %query, type: %type) {
            |            id
            |            title {
            |                native
            |                romaji
            |                english
            |            }
            |            coverImage {
            |                extraLarge
            |                large
            |            }
            |            format
            |            status
            |            description
            |            startDate {
            |                year
            |                month
            |                day
            |            }
            |            genres
            |            studios {
            |                edges {
            |                    node {
            |                        name
            |                    }
            |                }
            |            }
            |            staff(perPage: 5) {
            |                edges {
            |                    node {
            |                        name {
            |                            full
            |                        }
            |                    }
            |                    role
            |                }
            |            }
            |        }
            |    }
            |}
        """.trimMargin().replace("%", "${'$'}")

        val payload = buildJsonObject {
            put("query", QUERY)
            putJsonObject("variables") {
                put("query", query)
                put("type", type.name)
            }
        }

        val data = with(json) {
            client.newCall(
                POST(API_URL, body = payload.toString().toRequestBody(jsonMime))
            ).execute().parseAs<ALSearchResult<ALSearchPage>>()
                .data.page.media
        }

        return data
    }

    suspend fun searchFromId(id: Long, type: AnilistSearchType): ALSearchItem {
        val QUERY = """
            |query media(%id: Int, %type: MediaType) {
            |    Media(id: %id, type: %type) {
            |        id
            |        title {
            |            native
            |            romaji
            |            english
            |        }
            |        coverImage {
            |            extraLarge
            |            large
            |        }
            |        format
            |        status
            |        description
            |        startDate {
            |            year
            |            month
            |            day
            |        }
            |        genres
            |        studios {
            |            edges {
            |                node {
            |                    name
            |                }
            |            }
            |        }
            |        staff(perPage: 5) {
            |            edges {
            |                node {
            |                    name {
            |                        full
            |                    }
            |                }
            |                role
            |            }
            |        }
            |    }
            |}
        """.trimMargin().replace("%", "${'$'}")

        val payload = buildJsonObject {
            put("query", QUERY)
            putJsonObject("variables") {
                put("id", id)
                put("type", type.name)
            }
        }

        val data = with(json) {
            client.newCall(
                POST(API_URL, body = payload.toString().toRequestBody(jsonMime))
            ).execute().parseAs<ALSearchResult<ALMedia>>()
                .data.media
        }

        return data
    }

    companion object {
        private const val API_URL = "https://graphql.anilist.co/"
    }
}
