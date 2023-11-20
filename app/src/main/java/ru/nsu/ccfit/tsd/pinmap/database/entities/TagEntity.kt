package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    val tagId: Long = 0
}