package xyz.secozzi.aniyomilocalmanager.domain.model

import xyz.secozzi.aniyomilocalmanager.presentation.compontents.DropdownItem

sealed class EpisodeType(
    override val displayName: String,
    override val id: Int,
    override val extraData: Int?,
) : DropdownItem {
    data class Regular(
        override val displayName: String = "Regular episode",
        override val id: Int = 1,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)

    data class Special(
        override val displayName: String = "Special",
        override val id: Int = 2,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)

    data class Credit(
        override val displayName: String = "Credit",
        override val id: Int = 3,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)

    data class Trailer(
        override val displayName: String = "Trailer",
        override val id: Int = 4,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)

    data class Parody(
        override val displayName: String = "Parody",
        override val id: Int = 5,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)

    data class Other(
        override val displayName: String = "Other",
        override val id: Int = 6,
        override val extraData: Int? = null,
    ) : EpisodeType(displayName, id, extraData)
}
