package ru.nsu.ccfit.tsd.pinmap.filter

import java.util.Date

class Filter {
    val textIncludes: MutableList<String> = ArrayList()
    val hasTags: MutableList<String> = ArrayList()
    var startDate: Date? = null
    var endDate: Date? = null
    var lowestMood: Byte? = null
    var highestMood: Byte? = null
}