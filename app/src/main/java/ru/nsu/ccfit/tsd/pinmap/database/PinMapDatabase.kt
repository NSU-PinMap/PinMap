package ru.nsu.ccfit.tsd.pinmap.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nsu.ccfit.tsd.pinmap.database.daos.PinDao
import ru.nsu.ccfit.tsd.pinmap.database.daos.TagDao
import ru.nsu.ccfit.tsd.pinmap.database.entities.PhotoEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinTagEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity

@Database(
    entities = [PinEntity::class, TagEntity::class, PhotoEntity::class, PinTagEntity::class],
    version = 1
)
abstract class PinMapDatabase : RoomDatabase() {
    abstract fun pinDao(): PinDao?
    abstract fun tagDao(): TagDao?
}