package com.themovielist.util

import android.content.ContentValues
import android.text.TextUtils

private const val SEPARATOR = "??"

fun ContentValues.getAsIntArray(columnName: String): IntArray {
    val stringContent = this.getAsString(columnName)
    return stringToIntArray(stringContent)
}

fun ContentValues.put(columnName: String, intArray: IntArray) {
    this.put(columnName, intArray.joinToString { SEPARATOR })
}

internal fun stringToIntArray(stringContent: String): IntArray {
    if (TextUtils.isEmpty(stringContent)) {
        return IntArray(0)
    }

    return stringContent.split(SEPARATOR).map { it.toInt() }.toIntArray()
}