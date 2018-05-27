package com.themovielist.repository.data

import android.arch.persistence.room.TypeConverter
import android.text.TextUtils
import java.util.*


class RoomConverters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun intArrayFromString(value: String?): IntArray? {
        if (TextUtils.isEmpty(value)) {
            return IntArray(0)
        }

        return value?.split(SEPARATOR)?.map { it.toInt() }?.toIntArray()
    }

    @TypeConverter
    fun stringFromIntArray(value: IntArray?): String? {
        return value?.joinToString(separator = SEPARATOR)
    }

    companion object {
        private const val SEPARATOR = "??"
    }
}
