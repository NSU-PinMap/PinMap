package ru.nsu.ccfit.tsd.pinmap.pins

import android.content.Context
import ru.nsu.ccfit.tsd.pinmap.database.SQLitePinStorage

class PinController private constructor(context: Context) {
    companion object {
        private var instance : PinController? = null

        fun  getController(context: Context): PinController {
            if (instance == null)
                instance = PinController(context)

            return instance!!
        }
    }

    private var pinStorage : PinStorage = SQLitePinStorage(context)
    private var pins : MutableList<Pin> = pinStorage.getAllPins()

    fun save(pin: Pin): Boolean {
        val newPin = pinStorage.save(pin) ?: return false

        return pins.add(newPin)
    }

    fun delete(pin: Pin) : Boolean {
        val isDeleted = pinStorage.delete(pin)

        if (isDeleted) {
            pins = pinStorage.getAllPins()
        }

        return isDeleted
    }

    fun getAllPins(): MutableList<Pin> {
        if (pins.isEmpty()) {
            pins = pinStorage.getAllPins()
        }

        return pins
    }

    fun getAllTags(): MutableList<String> {
        return pinStorage.getAllTags()
    }
}