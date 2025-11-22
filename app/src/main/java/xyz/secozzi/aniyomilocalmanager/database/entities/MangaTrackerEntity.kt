package xyz.secozzi.aniyomilocalmanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MangaTrackerEntity(
    @PrimaryKey
    val path: String,
    val mangabaka: Long? = null,
    val anilist: Long? = null,
    val mal: Long? = null,
)
