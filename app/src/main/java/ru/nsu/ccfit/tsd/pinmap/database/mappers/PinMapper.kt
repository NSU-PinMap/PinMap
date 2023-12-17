package ru.nsu.ccfit.tsd.pinmap.database.mappers

import ru.nsu.ccfit.tsd.pinmap.database.PinMapDatabase
import ru.nsu.ccfit.tsd.pinmap.database.entities.PinEntity
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import java.util.Date

class PinMapper {
    companion object {
        fun pinToEntity(db: PinMapDatabase, pin: Pin): PinEntity {
            val pinEntity = PinEntity(
                pin.name, pin.latitude, pin.longitude,
                pin.mood.toInt(), dateToTimestamp(pin.date), pin.description
            )
            if (pin.id != null) {
                pinEntity.pinId = pin.id!!.toLong()
            }

            return pinEntity
        }

        fun entityToPin(db: PinMapDatabase, pinEntity: PinEntity): Pin {
            val pin = Pin(pinEntity.name, pinEntity.latitude, pinEntity.longitude)
            pin.id = pinEntity.pinId.toInt()
            pin.description = pinEntity.description
            pin.mood = pinEntity.mood.toUByte()
            pin.date = dateFromTimestamp(pinEntity.date)
            pin.tags =
                db.tagDao()!!.getTagsByPinId(pin.id!!).map { tagEntity -> tagEntity.name }
                    .toMutableList()
            return pin
        }

        private fun dateFromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        private fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}