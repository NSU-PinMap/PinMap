package ru.nsu.ccfit.tsd.pinmap.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class TagEntity(
    var name: String
) {
    @PrimaryKey(autoGenerate = true)
    var tagId: Long = 0
}