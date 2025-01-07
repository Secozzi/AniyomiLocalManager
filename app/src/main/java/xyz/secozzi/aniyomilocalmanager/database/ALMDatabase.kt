package xyz.secozzi.aniyomilocalmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.secozzi.aniyomilocalmanager.database.dao.TrackerIdDao
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity

@Database(entities = [TrackerIdEntity::class], version = 1)
abstract class ALMDatabase : RoomDatabase() {
    abstract fun trackerIdDao(): TrackerIdDao
}
