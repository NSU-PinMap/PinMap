package ru.nsu.ccfit.tsd.pinmap.pins

class PinController {
    var pins : MutableList<Pin> = ArrayList()
    // var pinRepository : PinRepository() TODO: Подключить класс, отвечающий за работу с базой данных

    fun save(pin: Pin): Boolean {
        /*
        val newPin = pinRepository.save(pin) TODO: Отправлять пин для сохранения в базу данных

        if (newPin == null) {
            return false
        } else {
            pins.add(newPin)
            return true
        }
        */

        return false
    }

    fun delete(pin: Pin) : Boolean {
        // return pinRepository.delete(pin) TODO: Отправлять апрос на удаление pin
        return false
    }

    fun getAllPins(): MutableList<Pin> {
        if (pins.isEmpty()) {
            // pins = pinRepository.getAllPins() TODO: Получать все пины из базы данных
        }

        return pins
    }

}