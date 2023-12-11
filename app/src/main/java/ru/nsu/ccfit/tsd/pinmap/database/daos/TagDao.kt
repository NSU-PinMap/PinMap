package ru.nsu.ccfit.tsd.pinmap.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinTagEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity

@Dao
interface TagDao {

    @Query("SELECT * FROM tag WHERE name = :name")
    fun getTagByName(name: String): TagEntity

    @Query("SELECT EXISTS(SELECT * FROM tag WHERE name = :name)")
    fun doesTagExists(name: String): Boolean

    @Insert
    fun insertTag(tagEntity: TagEntity): Long

    @Insert
    fun insertPinTag(pinTagEntity: PinTagEntity): Long

    @Query("SELECT * FROM tag")
    fun getAllTags(): List<TagEntity>

    @Query("SELECT name FROM tag")
    fun getAllTagsNames(): List<String>

    @Query(
        "SELECT tag.tagId, tag.name FROM tag " +
                "JOIN pin_tag ON pin_tag.tagId = tag.tagId " +
                "WHERE pin_tag.pinId = :id"
    )
    fun getTagsByPinId(id: Int): List<TagEntity>

    @Delete
    fun deleteTag(tagEntity: TagEntity)

    @Query(
        "DELETE FROM pin_tag WHERE pinId = :pinId"
    )
    fun deletePinTags(pinId: Long)
}
