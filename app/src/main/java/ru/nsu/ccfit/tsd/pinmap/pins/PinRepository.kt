package ru.nsu.ccfit.tsd.pinmap.pins

interface PinRepository {
    fun save(pin: Pin) : Pin?
    fun delete(pin: Pin) : Boolean
    fun getAllPins() : MutableList<Pin>
    fun getAllTags() : MutableList<String>
}