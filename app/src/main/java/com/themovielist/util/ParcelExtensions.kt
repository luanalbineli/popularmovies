package com.themovielist.util

import android.os.Parcel
import java.util.*

private const val NULL_VALUE = Byte.MIN_VALUE

fun Parcel.writeIntArrayWithLength(intArray: IntArray) {
    this.writeInt(intArray.size)
    this.writeIntArray(intArray)
}

fun Parcel.readIntArray(): IntArray {
    val intArray = IntArray(this.readInt())
    this.readIntArray(intArray)
    return intArray
}

// ##################### INT #####################

fun Parcel.writeNullableInt(value: Int?) {
    this.writeByte(if (value == null) NULL_VALUE else 0)
    if (value != null) {
        this.writeInt(value)
    }
}

fun Parcel.readNullableInt(): Int? {
    if (this.readByte() == NULL_VALUE) {
        return null
    }
    return this.readInt()
}

// ##################### BOOLEAN #####################

fun Parcel.readBoolean(): Boolean = this.readInt() == 1

fun Parcel.writeBoolean(boolean: Boolean) {
    this.writeInt(if (boolean) 1 else 0)
}

// ##################### DATE #####################

fun Parcel.readNullableDate(): Date? {
    val nullableDate = this.readLong()
    return if (nullableDate == NULL_VALUE.toLong()) {
        null
    } else {
        Date(nullableDate)
    }
}

fun Parcel.writeNullableDate(date: Date?) {
    this.writeLong(date?.time ?: NULL_VALUE.toLong())
}