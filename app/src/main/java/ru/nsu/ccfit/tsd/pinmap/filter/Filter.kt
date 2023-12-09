package ru.nsu.ccfit.tsd.pinmap.filter

import java.util.Date

class Filter {
    val textIncludes: List<String> = ArrayList()
    val hasTags: List<String> = ArrayList()
    var startDate: Date? = null
    var endDate: Date? = null
    val lowestMood: Byte? = null
    val highestMood: Byte? = null
}