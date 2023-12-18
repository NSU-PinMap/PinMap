package ru.nsu.ccfit.tsd.pinmap.pins

import ru.nsu.ccfit.tsd.pinmap.filter.Filter

interface PinStorage {
    fun save(pin: Pin) : Pin?
    fun delete(pin: Pin) : Boolean
    fun getAllPins() : MutableList<Pin>
    fun getAllTags() : MutableList<String>
    fun getPinById(id: Int): Pin?
    fun getFilteredPins(filter: Filter): MutableList<Pin>
}