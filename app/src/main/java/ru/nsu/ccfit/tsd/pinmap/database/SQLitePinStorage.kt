package ru.nsu.ccfit.tsd.pinmap.database

import android.content.Context
import android.net.Uri
import androidx.room.Room.databaseBuilder
import ru.nsu.ccfit.tsd.pinmap.database.entities.PhotoEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinPhotoEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinTagEntity
import ru.nsu.ccfit.tsd.pinmap.database.entities.TagEntity
import ru.nsu.ccfit.tsd.pinmap.database.mappers.PinMapper
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinStorage
import java.net.URI

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
        savePhotos(id, pin.photos)

        pin.id = id.toInt()
        return pin
    }

    override fun delete(pin: Pin): Boolean {
        db.pinDao()!!.deletePin(PinMapper.pinToEntity(db, pin))
        deletePinTags(pin.id!!.toLong())
        deletePinPhotos(pin.id!!.toLong())

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

    private fun savePhotos(pinId: Long, uriList: MutableList<Uri>) {
        for (uri in uriList) {
            val uriString = uri.toString()
            if (db.photoDao()!!.doesPhotoExists(uriString)) {
                db.photoDao()!!.insertPinPhoto(
                    PinPhotoEntity(
                        pinId,
                        db.photoDao()!!.getPhotoByUri(uriString).photoId
                    )
                )
            } else {
                val photoId = db.photoDao()!!.insertPhoto(PhotoEntity(uriString))
                db.photoDao()!!.insertPinPhoto(PinPhotoEntity(pinId, photoId))
            }
        }
    }

    private fun deletePinTags(pinId: Long) {
        db.tagDao().deletePinTags(pinId)
    }

    private fun deletePinPhotos(pinId: Long) {
        db.photoDao().deletePinPhotos(pinId)
    }

}