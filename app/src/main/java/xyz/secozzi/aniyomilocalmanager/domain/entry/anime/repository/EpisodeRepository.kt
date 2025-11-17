package xyz.secozzi.aniyomilocalmanager.domain.entry.anime.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType

interface EpisodeRepository {
    suspend fun getEpisodesFromId(id: String): ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>
}
