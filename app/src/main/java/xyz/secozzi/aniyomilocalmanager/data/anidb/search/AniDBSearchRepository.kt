package xyz.secozzi.aniyomilocalmanager.data.anidb.search

import okhttp3.Headers
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import xyz.secozzi.aniyomilocalmanager.data.anidb.search.dto.ADBAnime
import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepository
import xyz.secozzi.aniyomilocalmanager.utils.GET

class AniDBSearchRepository(
    override val id: Long,
    private val client: OkHttpClient,
) : SearchRepository {
    private val noRedirectClient = client.newBuilder()
        .followRedirects(false)
        .build()

    private val headers = Headers.headersOf(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
        "User-Agent",
        "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0",
    )

    override suspend fun search(query: String): List<SearchDataItem> {
        val resp = noRedirectClient.newCall(
            GET("https://anidb.net/anime/?adb.search=$query&do.search=1", headers),
        ).execute()

        if (resp.code == 302) {
            return getFromSingle(resp.headers["location"]!!)
        }

        val document = Jsoup.parse(resp.body.string(), resp.request.url.toString())

        return document.select("table#animelist > tbody > tr").map { entry ->
            val entryLink = entry.selectFirst("td.name a")!!

            ADBAnime(
                remoteId = entryLink.attr("href").substringAfterLast("/").toLong(),
                title = entryLink.text(),
                imageUrl = entry.selectFirst("td.thumb img[src]")?.attr("abs:src") ?: "",
                format = entry.selectFirst("td.type")?.text(),
                startDate = entry.selectFirst("td.airdate")?.let {
                    it.text().split(".").reversed().joinToString("-")
                },
            )
        }
    }

    private fun getFromSingle(url: String): List<ADBAnime> {
        val resp = client.newCall(GET(url, headers)).execute()
        val document = Jsoup.parse(resp.body.string(), url)

        return listOf(
            ADBAnime(
                remoteId = url.substringAfterLast("/").toLong(),
                title = document.selectFirst("h1.anime")!!.text().substringAfter("Anime: "),
                imageUrl = document.selectFirst(".info .image img")?.attr("abs:src") ?: "",
                format = document.selectFirst(".pane tr.type:not(:contains(ignore))")?.let {
                    it.text().substringAfter("Type ").substringBefore(",")
                },
                startDate = document.selectFirst("span[itemprop=startDate]")?.let {
                    it.text().split(".").reversed().joinToString("-")
                },
            ),
        )
    }
}
