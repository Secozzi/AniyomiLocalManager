package xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto


import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType

@Serializable
@XmlSerialName("anime", "", "")
data class AniDBAnimeDto(
    val episodes: Episodes,
) {
    @Serializable
    @XmlSerialName("episodes", "", "")
    data class Episodes(
        @XmlElement(true)
        val episode: List<EpisodeDto>,
    )
}

@Serializable
@XmlSerialName("episode", "", "")
data class EpisodeDto(
    val epno: EpNoDto,
    val length: Length?,
    val airdate: AirDate?,
    val rating: Rating?,
    val title: List<Title>,
    val summary: Summary?,
) {
    @Serializable
    @XmlSerialName("epno", "", "")
    data class EpNoDto(
        @XmlSerialName("type", "", "")
        val type: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("length", "", "")
    data class Length(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("airdate", "", "")
    data class AirDate(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("rating", "", "")
    data class Rating(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("title", "", "")
    data class Title(
        @XmlSerialName("lang", "http://www.w3.org/XML/1998/namespace", "")
        val language: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("summary", "", "")
    data class Summary(
        @XmlValue(true)
        val value: String,
    )
}

fun EpisodeDto.toEpisodeModel(): EpisodeModel {
    return EpisodeModel(
        englishTitle = this.title.firstOrNull { it.language == "en" }?.value,
        romajiTitle = this.title.firstOrNull { it.language == "x-jat" }?.value,
        nativeTitle = this.title.firstOrNull { it.language == "ja" }?.value,
        episodeNumber = if (this.epno.type == "1") {
            this.epno.value.toInt()
        } else {
            this.epno.value.drop(1).toInt()
        },
        duration = this.length?.value?.toInt(),
        airingDate = this.airdate?.value,
        rating = this.rating?.value,
        summary = this.summary?.value,
        episodeType = when (this.epno.type) {
            "1" -> EpisodeType.Regular()
            "2" -> EpisodeType.Special()
            "3" -> EpisodeType.Credit()
            "4" -> EpisodeType.Trailer()
            "5" -> EpisodeType.Parody()
            else -> EpisodeType.Other()
        },
    )
}
