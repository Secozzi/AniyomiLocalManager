package xyz.secozzi.aniyomilocalmanager.data.search.anidb.dto

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("anime", "", "")
data class AniDBAnime(
    val type: AniDBAnimeType?,
    val startDate: AniDBStartDate?,
    val endDate: AniDBEndDate?,
    val titles: AniDBTitles?,
    val creators: AniDBCreators?,
    val description: AniDBDescription?,
    val picture: AniDBPicture?,
    val episode: AniDBEpisodes?,
) {
    @Serializable
    @XmlSerialName("type", "", "")
    data class AniDBAnimeType(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("startdate", "", "")
    data class AniDBStartDate(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("enddate", "", "")
    data class AniDBEndDate(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("titles", "", "")
    data class AniDBTitles(
        val title: List<AniDBTitle>,
    ) {
        @Serializable
        @XmlSerialName("title", "", "")
        data class AniDBTitle(
            @XmlSerialName("type", "", "")
            val type: String,
            @XmlSerialName("lang", "http://www.w3.org/XML/1998/namespace", "")
            val language: String,
            @XmlValue(true)
            val value: String,
        )
    }

    @Serializable
    @XmlSerialName("creators", "", "")
    data class AniDBCreators(
        val creators: List<AniDBCreator>,
    ) {
        @Serializable
        @XmlSerialName("name", "", "")
        data class AniDBCreator(
            @XmlSerialName("type", "", "")
            val type: String,
            @XmlValue(true)
            val value: String,
        )
    }

    @Serializable
    @XmlSerialName("description", "", "")
    data class AniDBDescription(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("picture", "", "")
    data class AniDBPicture(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("episodes", "", "")
    data class AniDBEpisodes(
        @XmlElement(true)
        val episode: List<AniDBEpisode>,
    )
}

@Serializable
@XmlSerialName("episode", "", "")
data class AniDBEpisode(
    val epno: AniDBEpisodeEpNo,
    val length: AniDBEpisodeLength?,
    val airdate: AniDBEpisodeAirDate?,
    val rating: AniDBEpisodeRating?,
    val title: List<AniDBEpisodeTitle>,
    val summary: AniDBEpisodeSummary?,
) {
    @Serializable
    @XmlSerialName("epno", "", "")
    data class AniDBEpisodeEpNo(
        @XmlSerialName("type", "", "")
        val type: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("length", "", "")
    data class AniDBEpisodeLength(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("airdate", "", "")
    data class AniDBEpisodeAirDate(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("rating", "", "")
    data class AniDBEpisodeRating(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("title", "", "")
    data class AniDBEpisodeTitle(
        @XmlSerialName("lang", "http://www.w3.org/XML/1998/namespace", "")
        val language: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("summary", "", "")
    data class AniDBEpisodeSummary(
        @XmlValue(true)
        val value: String,
    )
}
