package xyz.secozzi.aniyomilocalmanager.data.cover

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import xyz.secozzi.aniyomilocalmanager.domain.cover.repository.MappingRepository
import java.io.File

class MappingRepositoryImpl(
    private val context: Context,
    private val client: HttpClient,
    private val json: Json,
) : MappingRepository {
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getTvdbId(anilistId: Long?, malId: Long?, anidbId: Long?): Pair<String, Long>? {
        val currentTime = System.currentTimeMillis() / 1000L

        val cacheFile = File(context.cacheDir, MAPPING_CACHE_FILE_NAME).also {
            if (!it.exists()) {
                it.createNewFile()
                it.writeText(
                    json.encodeToString(CachedDto(lastChecked = 0L, data = emptyList())),
                )
            }
        }

        val fileData = cacheFile.inputStream().use { s ->
            json.decodeFromStream<CachedDto>(s)
        }

        // Check if exists in cache
        getMatch(fileData.data, anilistId, malId, anidbId)?.let {
            return it
        }

        if (currentTime < fileData.lastChecked + 3600 * 24) {
            return null
        }

        val data: List<MappingDto> = client.get(MAPPING_URL).body()
        File(context.cacheDir, MAPPING_CACHE_FILE_NAME).writeText(
            json.encodeToString(
                CachedDto(
                    lastChecked = currentTime,
                    data = data,
                ),
            ),
        )

        return getMatch(data, anilistId, malId, anilistId)
    }

    private fun getMatch(data: List<MappingDto>, anilistId: Long?, malId: Long?, anidbId: Long?): Pair<String, Long>? {
        data.forEach {
            if (it.thetvdbId != null) {
                if (it.type != null && (it.anilistId == anilistId || it.malId == malId || it.anidbId == anidbId)) {
                    return it.type to it.thetvdbId
                }
            }
        }

        return null
    }

    @Serializable
    data class CachedDto(
        val lastChecked: Long,
        val data: List<MappingDto>,
    )

    @Serializable
    data class MappingDto(
        val type: String? = null,
        @SerialName("anilist_id") val anilistId: Long? = null,
        @SerialName("mal_id") val malId: Long? = null,
        @SerialName("anidb_id") val anidbId: Long? = null,
        @SerialName("thetvdb_id") val thetvdbId: Long? = null,
    )

    companion object {
        private const val MAPPING_CACHE_FILE_NAME = "mapping-cache.json"
        private const val MAPPING_URL =
            "https://raw.githubusercontent.com/Fribb/anime-lists/master/anime-list-mini.json"
    }
}
