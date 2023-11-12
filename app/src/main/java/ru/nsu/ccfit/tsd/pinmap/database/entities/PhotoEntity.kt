package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class PhotoEntity(
    val uri: String
) {
    @PrimaryKey(autoGenerate = true) val photoId: Long = 0
}