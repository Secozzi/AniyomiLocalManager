package xyz.secozzi.aniyomilocalmanager.domain.entry.model

import androidx.annotation.StringRes
import xyz.secozzi.aniyomilocalmanager.R

enum class Status(val id: Int, @param:StringRes val stringRes: Int) {
    Unknown(0, R.string.status_unknown),
    Ongoing(1, R.string.status_ongoing),
    Completed(2, R.string.status_completed),
    Licensed(3, R.string.status_licensed),
    PublishingFinished(4, R.string.status_publishing_finished),
    Cancelled(5, R.string.status_cancelled),
    OnHiatus(6, R.string.status_on_hiatus),
}
