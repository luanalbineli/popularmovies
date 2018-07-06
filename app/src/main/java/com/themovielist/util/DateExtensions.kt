package com.themovielist.util

import java.util.*

val Date?.yearFromCalendar
    get() = getCalendarField(this, Calendar.YEAR)

fun getCalendarField(date: Date?, field: Int): Int? {
    if (date == null) {
        return null
    }
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(field)
}