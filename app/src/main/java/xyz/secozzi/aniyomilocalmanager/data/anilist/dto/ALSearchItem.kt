package xyz.secozzi.aniyomilocalmanager.data.anilist.dto

import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.domain.model.Status
import xyz.secozzi.aniyomilocalmanager.preferences.TitleLangs

@Serializable
data class ALSearchItem(
    val id: Long,
    val format: String?,
    val title: ALSearchItemTitleDto,
    val coverImage: ALSearchItemCoverDto,
    val status: String?,
    val description: String?,
    val startDate: ALSearchItemDateDto,
    val genres: List<String>,
    val studios: ALSearchItemEdge<ALSearchItemStudioNodeDto>,
    val staff: ALSearchItemEdge<ALSearchItemStaffNodeDto>,
) {
    fun toALAnime(titlePreference: TitleLangs, studioCountPreference: Int): ALAnime {
        return ALAnime(
            remoteId = id,
            titles = title.toTitle(titlePreference),
            imageUrl = coverImage.extraLarge ?: coverImage.large,
            description = description.cleanDescription(),
            format = format,
            publishingStatus = status.toStatus(),
            startDate = startDate.toDate(),
            genre = genres.joinToString(),
            studio = studios.edges.take(studioCountPreference).joinToString {
                it.node.name
            },
        )
    }

    fun toALManga(titlePreference: TitleLangs): ALManga {
        val authors = staff.edges.filter { it.role!! in authorRoles }.joinToString {
            it.node.name.full
        }
        val artists = staff.edges.filter { it.role!! in artistRoles }.joinToString {
            it.node.name.full
        }

        return ALManga(
            remoteId = id,
            titles = title.toTitle(titlePreference),
            imageUrl = coverImage.extraLarge ?: coverImage.large,
            author = authors,
            artist = artists,
            description = description.cleanDescription(),
            format = format,
            publishingStatus = status.toStatus(),
            startDate = startDate.toDate(),
            genre = genres.joinToString(),
        )
    }

    private fun ALSearchItemTitleDto.toTitle(titlePreference: TitleLangs): List<String> {
        return when (titlePreference) {
            TitleLangs.Native -> listOf(title.native, title.romaji, title.english)
            TitleLangs.Romaji -> listOf(title.romaji, title.english, title.native)
            TitleLangs.English -> listOf(title.english, title.romaji, title.native)
        }.filterNotNull()
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

    private fun ALSearchItemDateDto.toDate(): String {
        return listOfNotNull(this.year, this.month, this.day)
            .joinToString(separator = "-")
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
data class ALSearchItemCoverDto(
    val extraLarge: String?,
    val large: String,
)

@Serializable
data class ALSearchItemTitleDto(
    val romaji: String?,
    val english: String?,
    val native: String?,
)

@Serializable
data class ALSearchItemDateDto(
    val day: Int?,
    val month: Int?,
    val year: Int?,
)

@Serializable
data class ALSearchItemEdge<T>(
    val edges: List<ALSearchItemNode<T>>,
) {
    @Serializable
    data class ALSearchItemNode<T>(
        val node: T,
        val role: String? = null,
    )
}

@Serializable
data class ALSearchItemStudioNodeDto(
    val name: String,
)

@Serializable
data class ALSearchItemStaffNodeDto(
    val name: ALSearchItemStaffNameDto,
) {
    @Serializable
    data class ALSearchItemStaffNameDto(
        val full: String,
    )
}
