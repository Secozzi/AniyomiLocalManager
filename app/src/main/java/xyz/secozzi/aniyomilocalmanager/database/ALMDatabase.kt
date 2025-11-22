package xyz.secozzi.aniyomilocalmanager.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import xyz.secozzi.aniyomilocalmanager.database.dao.AnimeTrackerDao
import xyz.secozzi.aniyomilocalmanager.database.dao.MangaTrackerDao
import xyz.secozzi.aniyomilocalmanager.database.entities.AnimeTrackerEntity
import xyz.secozzi.aniyomilocalmanager.database.entities.MangaTrackerEntity

@Database(
    version = 2,
    entities = [
        AnimeTrackerEntity::class,
        MangaTrackerEntity::class,
    ],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = ALMDatabase.DeleteTrackerEntityMigration::class,
        ),
    ],
)
abstract class ALMDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeTrackerDao

    abstract fun mangaDao(): MangaTrackerDao

    @DeleteTable(tableName = "TrackerIdEntity")
    class DeleteTrackerEntityMigration : AutoMigrationSpec
}
