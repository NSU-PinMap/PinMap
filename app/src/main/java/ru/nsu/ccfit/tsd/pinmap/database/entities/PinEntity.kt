package ru.nsu.ccfit.tsd.pinmap.database.entities;

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pin")
data class PinEntity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val mood: Int,
    val date: Date?,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    val pinId: Long = 0
}