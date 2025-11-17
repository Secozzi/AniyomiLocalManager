package xyz.secozzi.aniyomilocalmanager.data.search.anidb

import android.content.Context
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import io.ktor.http.appendPathSegments
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto.AniDBAnime
import xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto.AniDBEpisode
import xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto.AniDBTitleAnime
import xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto.AniDBTitleAnimeTitle
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.repository.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.EntryDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.ktor.anidb.AniDBHttpClient
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import java.io.InputStream
import java.util.zip.GZIPInputStream

class AniDBSearch(
    private val context: Context,
    private val aniDBHttpClient: AniDBHttpClient,
    private val aniDBPreferences: AniDBPreferences,
    private val xml: XML,
) : SearchRepository, EpisodeRepository {
    override suspend fun search(query: String): List<SearchResultItem> {
        val body = withContext(Dispatchers.IO) { GZIPInputStream(aniDBHttpClient.get(TITLES_URL).inputStream()) }
        val titles = withContext(Dispatchers.Default) {
            val decoded = body.use { parseAniDBTitles(it) }

            // Perform fuzzy search
            val normalized = query.lowercase().trim()
            val bestMatching = mutableListOf<Pair<String, Int>>()
            for (anime in decoded) {
                val score = anime.title.maxOfOrNull {
                    FuzzySearch.tokenSetRatio(it.value, normalized)
                } ?: 0
                if (score < 50) continue

                val pair = anime.aid to score
                val index = bestMatching.indexOfFirst { it.second < score }
                if (index == -1) {
                    if (bestMatching.size < SEARCH_LIMIT) bestMatching.add(pair)
                } else {
                    bestMatching.add(index, pair)
                }
            }

            if (bestMatching.any { it.second == 100 }) {
                bestMatching.removeIf { it.second < 90 }
            }

            bestMatching.take(SEARCH_LIMIT)
        }

        return withContext(Dispatchers.IO) {
            titles.map { (aid, _) ->
                val anime = getAnidbAnime(aid)

                SearchResultItem(
                    titles = anime.titles.getTitles().map { it.value },
                    coverUrl = anime.picture?.value?.let { IMG_URL + it },
                    type = anime.type?.value,
                    status = anime.getStatus(),
                    description = anime.description.getDescription(),
                    startDate = anime.startDate.getDate(),
                    genres = emptyList(),
                    authors = anime.creators.getStudios(),
                    artists = emptyList(),
                    trackerIds = mapOf(
                        TrackerIds.Anidb to aid.toLong(),
                    ),
                )
            }
        }
    }

    override suspend fun getFromId(id: String): EntryDetails {
        val anime = getAnidbAnime(id)
        val titles = anime.titles.getTitles().map { it.value }

        return EntryDetails(
            title = titles.firstOrNull().orEmpty(),
            titles = titles.toPersistentList(),
            authors = anime.creators.getStudios().joinToString(),
            artists = "",
            description = anime.description.getDescription().orEmpty(),
            genre = "",
            status = anime.getStatus(),
        )
    }

    override suspend fun getEpisodesFromId(id: String): ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>> {
        val anime = getAnidbAnime(id)

        return anime.episode?.episode.orEmpty()
            .groupBy { it.epno.type }
            .mapKeys { (type, _) ->
                when (type) {
                    "1" -> EpisodeType.Regular
                    "2" -> EpisodeType.Special
                    "3" -> EpisodeType.Credit
                    "4" -> EpisodeType.Trailer
                    "5" -> EpisodeType.Parody
                    "6" -> EpisodeType.Other
                    else -> throw IllegalArgumentException("Invalid episode type: $type")
                }
            }
            .mapValues { (type, episodes) ->
                episodes.map { ep ->
                    EpisodeDetails(
                        episodeNumber = if (type == EpisodeType.Regular) {
                            ep.epno.value.toInt()
                        } else {
                            ep.epno.value.drop(1).toInt()
                        },
                        name = getEpisodeFromTemplate(ep, aniDBPreferences.nameFormat.get(), type),
                        dateUpload = ep.airdate?.value?.plus("T00:00:00"),
                        fillermark = false,
                        scanlator = getEpisodeFromTemplate(ep, aniDBPreferences.scanlatorFormat.get(), type),
                        summary = getEpisodeFromTemplate(ep, aniDBPreferences.summaryFormat.get(), type),
                        previewUrl = null,
                    )
                }
                    .sortedBy { it.episodeNumber }
                    .toPersistentList()
            }
            .toSortedMap()
            .toPersistentMap()
    }

    private fun getEpisodeFromTemplate(anidbEpisode: AniDBEpisode, template: String, type: EpisodeType): String? {
        return template
            .replace("%eng", anidbEpisode.title.firstOrNull { it.language == "en" }?.value ?: "")
            .replace("%rom", anidbEpisode.title.firstOrNull { it.language == "x-jat" }?.value ?: "")
            .replace("%nat", anidbEpisode.title.firstOrNull { it.language == "ja" }?.value ?: "")
            .replace("%ep", anidbEpisode.epno.value)
            .replace("%dur", anidbEpisode.length?.value ?: "")
            .replace("%air", anidbEpisode.airdate?.value ?: "")
            .replace("%rat", anidbEpisode.rating?.value ?: "")
            .replace("%sum", anidbEpisode.summary?.value ?: "")
            .replace("%type", context.getString(type.stringRes))
            .takeIf { it.isNotBlank() }
    }

    private fun AniDBAnime.AniDBTitles?.getTitles(): List<AniDBAnime.AniDBTitles.AniDBTitle> {
        return this?.title.orEmpty()
            .filter { it.language in TITLE_LANG_WHITELIST && it.type in TYPE_WHITELIST }
            .sortedBy {
                when (aniDBPreferences.prefLang.get()) {
                    LangPrefEnum.English -> it.language == "en"
                    LangPrefEnum.Romaji -> it.language == "x-jat"
                    LangPrefEnum.Native -> it.language == "ja"
                }
            }
            .reversed()
    }

    private fun AniDBAnime.AniDBStartDate?.getDate(): String? {
        return this?.value?.takeUnless { it == "1970-01-01" }
    }

    private fun AniDBAnime.AniDBDescription?.getDescription(): String? {
        return this?.value?.replace(REF_REGEX, "$1")
    }

    private fun AniDBAnime.AniDBCreators?.getStudios(): List<String> {
        return this?.creators.orEmpty()
            .filter { it.type.equals("animation work", true) }
            .map { it.value }
    }

    private fun AniDBAnime.getStatus(): Status {
        return when {
            this.startDate.getDate() == null -> Status.Unknown
            endDate == null -> Status.Ongoing
            else -> Status.Completed
        }
    }

    private suspend fun getAnidbAnime(aid: String): AniDBAnime {
        val data = aniDBHttpClient.get(API_URL) {
            url {
                appendPathSegments("httpapi")
                parameters.append("request", "anime")
                parameters.append("client", CLIENT_NAME)
                parameters.append("clientver", CLIENT_VER)
                parameters.append("protover", "1")
                parameters.append("aid", aid)
            }
        }

        return xml.decodeFromString<AniDBAnime>(data.toString(Charsets.UTF_8))
    }

    // Normal XML deserialization takes ~5 seconds, this takes ~800 ms
    private fun parseAniDBTitles(input: InputStream): List<AniDBTitleAnime> {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(input, null)

        val animeList = mutableListOf<AniDBTitleAnime>()

        var event = parser.eventType
        var currentAid: String? = null
        var titles: MutableList<AniDBTitleAnimeTitle>? = null

        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "anime" -> {
                            currentAid = parser.getAttributeValue("", "aid")
                            titles = mutableListOf()
                        }
                        "title" -> {
                            val lang = parser.getAttributeValue(
                                "http://www.w3.org/XML/1998/namespace",
                                "lang",
                            )
                            val type = parser.getAttributeValue("", "type")
                            val value = parser.nextText()

                            if (lang in TITLE_LANG_WHITELIST && type in TYPE_WHITELIST) {
                                titles?.add(AniDBTitleAnimeTitle(lang, value.lowercase().trim()))
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "anime") {
                        animeList.add(
                            AniDBTitleAnime(
                                aid = currentAid ?: "",
                                title = titles ?: emptyList(),
                            ),
                        )
                    }
                }
            }
            event = parser.next()
        }

        return animeList
    }

    companion object {
        private val REF_REGEX = Regex("""https?:\/\/anidb.net\S* \[([^\]]+)\]""")
        private val TITLE_LANG_WHITELIST = setOf("x-jat", "ja", "en")
        private val TYPE_WHITELIST = setOf("main", "official")

        private const val SEARCH_LIMIT = 6
        private const val CLIENT_NAME = "localfipsklkxaay"
        private const val CLIENT_VER = "1"
        private const val API_URL = "http://api.anidb.net:9001"
        private const val TITLES_URL = "http://anidb.net/api/anime-titles.xml.gz"
        private const val IMG_URL = "https://cdn-eu.anidb.net/images/main/"
    }
}
