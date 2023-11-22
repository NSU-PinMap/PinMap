package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class PhotoEntity(
    var uri: String
) {
    @PrimaryKey(autoGenerate = true) var photoId: Long = 0
}