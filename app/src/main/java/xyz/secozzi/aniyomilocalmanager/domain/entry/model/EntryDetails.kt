package xyz.secozzi.aniyomilocalmanager.domain.entry.model

data class EntryDetails(
    val title: String,
    val titles: List<String>,
    val authors: String,
    val artists: String,
    val description: String,
    val genre: String,
    val status: Status,
) {
    companion object {
        val Empty = EntryDetails(
            title = "",
            titles = emptyList(),
            authors = "",
            artists = "",
            description = "",
            genre = "",
            status = Status.Unknown,
        )
    }
}
