package com.themovielist.util

import java.util.*

/*var DEFAULT_DATE_FORMAT: SimpleDateFormat? = null


fun Date?.toDefaultDateFormat(context: Context): String? {
    if (this == null) {
        return null
    }

    return DEFAULT_DATE_FORMAT?.format(this) ?: {
        DEFAULT_DATE_FORMAT = SimpleDateFormat(context.getString(R.string.default_date_format), Locale.getDefault())
        DEFAULT_DATE_FORMAT!!.format(this)
    }()
}*/

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
