package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity

@Entity(tableName = "pin_photo", primaryKeys = ["pinId", "photoId"])
data class PinPhotoEntity(
    var pinId: Int,
    var photoId: Int
)
