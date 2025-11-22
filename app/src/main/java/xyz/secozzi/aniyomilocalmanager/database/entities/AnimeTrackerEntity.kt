package xyz.secozzi.aniyomilocalmanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnimeTrackerEntity(
    @PrimaryKey
    val path: String,
    val anilist: Long? = null,
    val anidb: Long? = null,
    val mal: Long? = null,
)
