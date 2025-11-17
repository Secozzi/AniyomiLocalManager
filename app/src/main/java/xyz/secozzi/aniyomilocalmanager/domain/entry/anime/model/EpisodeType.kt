package xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model

import androidx.annotation.StringRes
import xyz.secozzi.aniyomilocalmanager.R

enum class EpisodeType(@param:StringRes val stringRes: Int) {
    Regular(R.string.episode_type_regular),
    Special(R.string.episode_type_special),
    Credit(R.string.episode_type_credit),
    Trailer(R.string.episode_type_trailer),
    Parody(R.string.episode_type_parody),
    Other(R.string.episode_type_other),
}
