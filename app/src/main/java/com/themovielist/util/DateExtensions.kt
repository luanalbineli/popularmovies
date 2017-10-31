package com.themovielist.util

import android.content.Context
import android.text.format.DateFormat
import com.albineli.udacity.popularmovies.R
import java.text.SimpleDateFormat
import java.util.*

var DEFAULT_DATE_FORMAT: SimpleDateFormat? = null


fun Date?.toDefaultDateFormat(context: Context): String? {
    if (this == null) {
        return null
    }

    return DEFAULT_DATE_FORMAT?.format(this) ?: {
        DEFAULT_DATE_FORMAT = SimpleDateFormat(context.getString(R.string.default_date_format), Locale.getDefault())
        DEFAULT_DATE_FORMAT!!.format(this)
    }()
}