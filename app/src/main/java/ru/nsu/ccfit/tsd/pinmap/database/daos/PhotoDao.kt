package ru.nsu.ccfit.tsd.pinmap.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.nsu.ccfit.tsd.pinmap.database.entities.PhotoEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinPhotoEntity

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photo WHERE uri = :uri")
    fun getPhotoByUri(uri: String): PhotoEntity

    @Query("SELECT EXISTS(SELECT * FROM photo WHERE uri = :uri)")
    fun doesPhotoExists(uri: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photoEntity: PhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPinPhoto(pinPhotoEntity: PinPhotoEntity): Long

    @Query(
        "SELECT photo.photoId, photo.uri FROM photo " +
                "JOIN pin_photo ON pin_photo.photoId = photo.photoId " +
                "WHERE pin_photo.pinId = :id"
    )
    fun getPhotosByPinId(id: Int): List<PhotoEntity>

    @Delete
    fun deletePhoto(photoEntity: PhotoEntity)

    @Query(
        "DELETE FROM pin_photo WHERE pinId = :pinId"
    )
    fun deletePinPhotos(pinId: Long)
}
