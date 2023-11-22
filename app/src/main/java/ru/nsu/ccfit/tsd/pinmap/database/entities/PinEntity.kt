package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pin")
data class PinEntity(
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var mood: Int,
    var date: Long?,
    var description: String
) {
    @PrimaryKey(autoGenerate = true)
    var pinId: Long = 0
}