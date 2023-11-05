package ru.nsu.ccfit.tsd.pinmap.pins

class PinController {
    var pins : MutableList<Pin> = ArrayList()
    /* var pinRepository : PinRepository TODO: Подключить конкретный класс,
                                           отвечающий за работу с базой данных, весь код
                                           раскомментировать */

    fun save(pin: Pin): Boolean {
        /*
        val newPin = pinRepository.save(pin)

        if (newPin == null)
            return false

        pins.add(newPin)

        return true

        */
        return false
    }

    fun delete(pin: Pin) : Boolean {
        // return pinRepository.delete(pin)
        return false
    }

    fun getAllPins(): MutableList<Pin> {
        if (pins.isEmpty()) {
            // pins = pinRepository.getAllPins()
        }

        return pins
    }

}