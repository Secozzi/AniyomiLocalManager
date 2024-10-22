package xyz.secozzi.aniyomilocalmanager.domain.model

import xyz.secozzi.aniyomilocalmanager.presentation.compontents.DropdownItem

enum class Status(
    override val displayName: String,
    override val id: Int,
    override val extraData: Int?,
) : DropdownItem {
    Unknown("Unknown", 0, null),
    Ongoing("Ongoing", 1, null),
    Completed("Completed", 2, null),
    Licensed("Licensed", 3, null),
    PublishingFinished("Publishing finished", 4, null),
    Cancelled("Cancelled", 5, null),
    OnHiatus("On hiatus", 6, null),
}
