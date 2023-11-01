package ru.nsu.ccfit.tsd.pinmap.pincontroller

import java.util.Date

class Pin(
    var id: Int,
    var name: String,
    var location: Int /* TODO: прописать самим локацию или использовать андроидовский класс?
                            В случае 2 реализованы все возможности, но есть привязка к андроиду*/
)
{
    var tags: MutableList<String> = ArrayList() /* TODO: вынести теги в класс или оставить так?
                                        В случае 1 можно использовать теги как сущности
                                        для поиска в программе*/
    var description: String? = null
    var mood: UByte? = null
    var date: Date? = null
}

