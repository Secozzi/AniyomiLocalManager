package xyz.secozzi.aniyomilocalmanager.domain.preferences

import androidx.annotation.StringRes
import xyz.secozzi.aniyomilocalmanager.R

enum class LangPrefEnum(@param:StringRes val titleRes: Int) {
    English(R.string.pref_english),
    Romaji(R.string.pref_romaji),
    Native(R.string.pref_native),
}
