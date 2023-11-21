package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity

@Entity(tableName = "pin_tag", primaryKeys = ["pinId", "tagId"])
data class PinTagEntity(
    var pinId: Long,
    var tagId: Long
)
