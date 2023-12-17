package ru.nsu.ccfit.tsd.pinmap.pins

import android.content.Context
import ru.nsu.ccfit.tsd.pinmap.database.SQLitePinStorage
import ru.nsu.ccfit.tsd.pinmap.filter.Filter

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

        //pins.add(newPin) TODO: replace with insert/add

        return true
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

        return pinStorage.getAllPins()
    }

    fun getAllTags(): MutableList<String> {
        return pinStorage.getAllTags()
    }

    fun getFilteredPins(filter: Filter): MutableList<Pin> {
        //return getFilteredPins(filter)
        return getAllPins() // TODO: Реализовать поиск по параметрам
    }

    fun getPinById(id: Int): Pin? {
        return pinStorage.getPinById(id) // TODO: Проверить корректность реализации
    }
}