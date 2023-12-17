package ru.nsu.ccfit.tsd.pinmap.database

import android.content.Context
import androidx.room.Room.databaseBuilder
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinTagEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity
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
        if(pin.id != null) {
            deletePinTags(pin.id!!.toLong())
        }
        saveTags(id, pin.tags)
        pin.id = id.toInt()
        return pin
    }

    override fun delete(pin: Pin): Boolean {
        db.pinDao()!!.deletePin(PinMapper.pinToEntity(db, pin))
        deletePinTags(pin.id!!.toLong())
        return true
    }

    override fun getAllPins(): MutableList<Pin> {
        return db.pinDao()!!.getAllPins().map { pinEntity -> PinMapper.entityToPin(db, pinEntity) }
            .toMutableList()
    }

    override fun getAllTags(): MutableList<String> {
        return db.tagDao()!!.getAllTagsNames().toMutableList()
    }

    override fun getPinById(id: Int): Pin {
        return PinMapper.entityToPin(db, db.pinDao()!!.getPinById(id))
    }

    private fun saveTags(pinId: Long, tags: MutableList<String>) {
        for (tag in tags) {
            if (db.tagDao()!!.doesTagExists(tag)) {
                db.tagDao()!!.insertPinTag(
                    PinTagEntity(
                        pinId,
                        db.tagDao()!!.getTagByName(tag).tagId
                    )
                )
            } else {
                val tagId = db.tagDao()!!.insertTag(TagEntity(tag))
                db.tagDao()!!.insertPinTag(PinTagEntity(pinId, tagId))
            }
        }
    }

    private fun deletePinTags(pinId: Long) {
        db.tagDao().deletePinTags(pinId)
    }

}