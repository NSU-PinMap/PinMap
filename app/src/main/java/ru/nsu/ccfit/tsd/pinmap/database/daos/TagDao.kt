package ru.nsu.ccfit.tsd.pinmap.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity

@Dao
interface TagDao {
    @Insert
    fun insertTag(tagEntity: TagEntity) : Long

    @Query("SELECT * FROM tag")
    fun getAllTags(): List<TagEntity>

    @Query("SELECT name FROM tag")
    fun getAllTagsNames(): List<String>

    @Query("SELECT tag.tagId, tag.name FROM pin_tag " +
            "JOIN tag ON pin_tag.tagId = tag.tagId " +
            "WHERE pin_tag.pinId = :id")
    fun getTagsByPinId(id: Int): List<TagEntity>

    @Delete
    fun deleteTag(tagEntity: TagEntity)
}
