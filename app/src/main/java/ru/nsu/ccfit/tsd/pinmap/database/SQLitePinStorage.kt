package ru.nsu.ccfit.tsd.pinmap.database

import android.content.Context
import androidx.room.Room.databaseBuilder
import ru.nsu.ccfit.tsd.pinmap.database.mappers.PinMapper
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinStorage

class SQLitePinStorage(context: Context) : PinStorage {

    private var db: PinMapDatabase =
        databaseBuilder(context, PinMapDatabase::class.java, "pinmapdb")
            .allowMainThreadQueries()
            .build()

    override fun save(pin: Pin): Pin? {
        val id = db.pinDao()!!.insertPin(PinMapper.pinToEntity(db, pin))
        pin.id = id.toInt()
        return pin
    }

    override fun delete(pin: Pin): Boolean {
        db.pinDao()!!.deletePin(PinMapper.pinToEntity(db, pin))
        return true
    }

    override fun getAllPins(): MutableList<Pin> {
        return db.pinDao()!!.getAllPins().map { pinEntity -> PinMapper.entityToPin(db, pinEntity) }
            .toMutableList()
    }

    override fun getAllTags(): MutableList<String> {
        return db.tagDao()!!.getAllTagsNames().toMutableList()
    }
}