package ru.nsu.ccfit.tsd.pinmap.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinEntity

@Dao
interface PinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPin(pinEntity: PinEntity): Long

    @Update
    fun updatePin(pinEntity: PinEntity)

    @Query("SELECT * FROM pin WHERE pinId = :id")
    fun getPinById(id: Int): PinEntity

    @Query("SELECT * FROM pin")
    fun getAllPins(): List<PinEntity>

    @Delete
    fun deletePin(pinEntity: PinEntity)

    @Query("SELECT EXISTS(SELECT * FROM pin WHERE pinId = :id)")
    fun doesPinExists(id: Int): Boolean
}
