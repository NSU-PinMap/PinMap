package ru.nsu.ccfit.tsd.pinmap.pins

import java.util.Date

class Pin(
    var id: Int,
    var name: String,
    var latitude: Double,
    var longitude: Double
)
{
    var tags: MutableList<String> = ArrayList() /* TODO: вынести теги в класс или оставить так?
                                                    В случае 1 можно использовать теги как сущности
                                                    для поиска в программе */
    var description: String = ""
    var mood: UByte = 0u
    var date: Date? = null
}

