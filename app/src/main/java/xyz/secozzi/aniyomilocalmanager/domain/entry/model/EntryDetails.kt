package xyz.secozzi.aniyomilocalmanager.domain.entry.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class EntryDetails(
    val title: String,
    val titles: ImmutableList<String>,
    val authors: String,
    val artists: String,
    val description: String,
    val genre: String,
    val status: Status,
) {
    companion object {
        val Empty = EntryDetails(
            title = "",
            titles = persistentListOf(),
            authors = "",
            artists = "",
            description = "",
            genre = "",
            status = Status.Unknown,
        )
    }
}
