package ru.nsu.ccfit.tsd.pinmap.pins

import ru.nsu.ccfit.tsd.pinmap.database.PinMapDatabase
import ru.nsu.ccfit.tsd.pinmap.database.mappers.PinMapper

class SQLitePinStorage(val db : PinMapDatabase) : PinStorage {
    override fun save(pin: Pin): Pin? {
        val id = db.pinDao().insertPin(PinMapper.pinToEntity(db, pin))
        //pin.setId(id)
        return pin
    }

    override fun delete(pin: Pin): Boolean {
        db.pinDao().deletePin(PinMapper.pinToEntity(db, pin));
        return true;
    }

    override fun getAllPins(): MutableList<Pin> {
        return db.pinDao().getAllPins().map { pinEntity -> PinMapper.entityToPin(db, pinEntity)}.toMutableList();
    }

    override fun getAllTags(): MutableList<String> {
        return db.tagDao().getAllTagsNames().toMutableList()
    }
}