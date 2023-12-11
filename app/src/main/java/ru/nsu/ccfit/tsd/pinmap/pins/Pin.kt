package ru.nsu.ccfit.tsd.pinmap.pins

import android.net.Uri
import java.util.Date

class Pin(
    var name: String,
    var latitude: Double,
    var longitude: Double
) {
    var id: Int? = null
    var tags: MutableList<String> = ArrayList()
    var photos: MutableList<Uri> = ArrayList()
    var description: String = ""
    var mood: UByte = 0u
    var date: Date? = null
}

