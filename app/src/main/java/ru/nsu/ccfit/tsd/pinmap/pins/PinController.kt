package ru.nsu.ccfit.tsd.pinmap.pins

class PinController {
    /* var pinStorage : PinStorage TODO: Подключить конкретный класс,
                                           отвечающий за работу с базой данных, весь код
                                           раскомментировать */
    var pins : MutableList<Pin> = ArrayList() // pinStorage.getAllPins()

    fun save(pin: Pin): Boolean {
        /*
        val newPin = pinStorage.save(pin)

        if (newPin == null)
            return false

        pins.add(newPin)

        return true

        */
        return false
    }

    fun delete(pin: Pin) : Boolean {
        // return pinStorage.delete(pin)
        return false
    }

    fun getAllPins(): MutableList<Pin> {
        if (pins.isEmpty()) {
            // pins = pinStorage.getAllPins()
        }

        return pins
    }

}