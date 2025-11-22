package xyz.secozzi.aniyomilocalmanager.domain.entry.manga.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import nl.adaptivity.xmlutil.util.CompactFragment
import xyz.secozzi.aniyomilocalmanager.domain.entry.model.Status

// https://anansi-project.github.io/docs/comicinfo/schemas/v2.0
@Suppress("UNUSED")
@Serializable
@XmlSerialName("ComicInfo", "", "")
data class ComicInfo(
    // For chapters
    val title: Title?,
    val number: Number?,
    val translator: Translator?,

    // For manga
    val series: Series?,
    val summary: Summary?,
    val writer: Writer?,
    val penciller: Penciller?,
    val genre: Genre?,
    val publishingStatus: PublishingStatusTachiyomi?,

    @XmlValue
    val unknownChildren: List<CompactFragment> = emptyList(),
) {
    @XmlElement(false)
    @XmlSerialName("xmlns:xsd", "", "")
    val xmlSchema: String = "http://www.w3.org/2001/XMLSchema"

    @XmlElement(false)
    @XmlSerialName("xmlns:xsi", "", "")
    val xmlSchemaInstance: String = "http://www.w3.org/2001/XMLSchema-instance"

    @Serializable
    @XmlSerialName("Title", "", "")
    data class Title(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Number", "", "")
    data class Number(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Translator", "", "")
    data class Translator(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Series", "", "")
    data class Series(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Summary", "", "")
    data class Summary(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Writer", "", "")
    data class Writer(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Penciller", "", "")
    data class Penciller(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("Genre", "", "")
    data class Genre(@XmlValue(true) val value: String = "")

    // The spec doesn't have a good field for this
    @Serializable
    @XmlSerialName("PublishingStatusTachiyomi", "http://www.w3.org/2001/XMLSchema", "ty")
    data class PublishingStatusTachiyomi(@XmlValue(true) val value: String = "")

    companion object {
        val EMPTY = ComicInfo(
            title = null,
            number = null,
            translator = null,
            series = null,
            summary = null,
            writer = null,
            penciller = null,
            genre = null,
            publishingStatus = null,
        )
    }
}

enum class ComicInfoPublishingStatus(
    val comicInfoValue: String,
    val statusValue: Status,
) {
    ONGOING("Ongoing", Status.Ongoing),
    COMPLETED("Completed", Status.Completed),
    LICENSED("Licensed", Status.Licensed),
    PUBLISHING_FINISHED("Publishing finished", Status.PublishingFinished),
    CANCELLED("Cancelled", Status.Cancelled),
    ON_HIATUS("On hiatus", Status.OnHiatus),
    UNKNOWN("Unknown", Status.Unknown),
    ;

    companion object {
        fun toComicInfoValue(value: Status): String {
            return entries.firstOrNull { it.statusValue == value }?.comicInfoValue
                ?: UNKNOWN.comicInfoValue
        }

        fun toStatusValue(value: String?): Status {
            return entries.firstOrNull { it.comicInfoValue == value }?.statusValue
                ?: Status.Unknown
        }
    }
}
