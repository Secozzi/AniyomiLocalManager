package xyz.secozzi.aniyomilocalmanager.data.search.anilist.dto

import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status
import xyz.secozzi.aniyomilocalmanager.domain.preferences.LangPrefEnum
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds

@Serializable
data class AnilistSearchItem(
    val id: Long,
    val idMal: Long?,
    val format: String?,
    val title: AnilistSearchItemTitleDto,
    val coverImage: AnilistSearchItemCoverDto,
    val status: String?,
    val description: String?,
    val startDate: AnilistSearchItemDateDto,
    val genres: List<String>,
    val studios: AnilistSearchItemEdge<AnilistSearchItemStudioNodeDto>,
    val staff: AnilistSearchItemEdge<AnilistSearchItemStaffNodeDto>,
) {
    fun toSearchResultItem(
        titlePreference: LangPrefEnum,
        studioCountPreference: Int,
        isManga: Boolean,
    ): SearchResultItem {
        val authors = if (isManga) {
            staff.edges.filter { it.role!! in authorRoles }.map { it.node.name.full }.take(studioCountPreference)
        } else {
            studios.edges.take(studioCountPreference).map { it.node.name }
        }
        val artists = if (isManga) {
            staff.edges.filter { it.role!! in artistRoles }.map { it.node.name.full }.take(studioCountPreference)
        } else {
            emptyList()
        }

        return SearchResultItem(
            titles = getTitle(titlePreference),
            coverUrl = coverImage.extraLarge ?: coverImage.large,
            type = format,
            status = status.toStatus(),
            description = description.cleanDescription(),
            startDate = startDate.toDate(),
            genres = genres,
            authors = authors,
            artists = artists,
            trackerIds = buildMap {
                put(TrackerIds.Anilist, id)
                idMal?.let {
                    put(TrackerIds.Mal, it)
                }
            },
        )
    }

    private fun getTitle(titlePreference: LangPrefEnum): List<String> {
        return when (titlePreference) {
            LangPrefEnum.Native -> listOf(title.native, title.romaji, title.english)
            LangPrefEnum.Romaji -> listOf(title.romaji, title.english, title.native)
            LangPrefEnum.English -> listOf(title.english, title.romaji, title.native)
        }.filterNotNull()
    }

    private fun AnilistSearchItemDateDto.toDate(): String {
        return listOfNotNull(this.year, this.month, this.day)
            .joinToString(separator = "-")
    }

    private fun String?.toStatus(): Status {
        return when (this?.lowercase()) {
            "finished" -> Status.Completed
            "releasing" -> Status.Ongoing
            "cancelled" -> Status.Cancelled
            "hiatus" -> Status.OnHiatus
            else -> Status.Unknown
        }
    }

    private fun String?.cleanDescription(): String? {
        return this
            ?.replace("\n", "")
            ?.replace("<br>", "\n")
            ?.replace(tagRegex, "")
    }

    companion object {
        val authorRoles = arrayOf("Story", "Story&Art", "Story & Art")
        val artistRoles = arrayOf("Art", "Story&Art", "Story & Art")

        val tagRegex = Regex("""<\/?\w+>""")
    }
}

@Serializable
data class AnilistSearchItemCoverDto(
    val extraLarge: String?,
    val large: String,
)

@Serializable
data class AnilistSearchItemTitleDto(
    val romaji: String?,
    val english: String?,
    val native: String?,
)

@Serializable
data class AnilistSearchItemDateDto(
    val day: Int?,
    val month: Int?,
    val year: Int?,
)

@Serializable
data class AnilistSearchItemEdge<T>(
    val edges: List<AnilistSearchItemNode<T>>,
) {
    @Serializable
    data class AnilistSearchItemNode<T>(
        val node: T,
        val role: String? = null,
    )
}

@Serializable
data class AnilistSearchItemStudioNodeDto(
    val name: String,
)

@Serializable
data class AnilistSearchItemStaffNodeDto(
    val name: AnilistSearchItemStaffNameDto,
) {
    @Serializable
    data class AnilistSearchItemStaffNameDto(
        val full: String,
    )
}
