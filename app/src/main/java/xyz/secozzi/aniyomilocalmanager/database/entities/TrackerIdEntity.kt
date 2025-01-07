package xyz.secozzi.aniyomilocalmanager.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackerIdEntity(
    @PrimaryKey
    val path: String,
    val anilistId: Long? = null,
    val aniDBId: Long? = null,
)
