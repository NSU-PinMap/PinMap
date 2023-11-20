package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity

@Entity(tableName = "pin_photo", primaryKeys = ["pinId", "photoId"])
data class PinPhotoEntity(
    val pinId: Int,
    val photoId: Int
)
