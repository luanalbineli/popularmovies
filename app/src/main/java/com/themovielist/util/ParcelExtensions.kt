package com.themovielist.util

import android.os.Parcel

private val NULL_VALUE = 1.toByte()

fun Parcel.writeIntArrayWithLength(intArray: IntArray) {
    this.writeInt(intArray.size)
    this.writeIntArray(intArray)
}

fun Parcel.readIntArray(): IntArray {
    val intArray = IntArray(this.readInt())
    this.readIntArray(intArray)
    return intArray
}

fun Parcel.writeNullableInt(value: Int?) {
    this.writeByte(if (value == null) NULL_VALUE else 0)
    if (value != null) {
        this.writeInt(value)
    }
}

fun Parcel.readBoolean(): Boolean = this.readInt() == 1

fun Parcel.writeBoolean(boolean: Boolean) {
    this.writeInt(if (boolean) 1 else 0)
}

fun Parcel.readNullableInt(): Int? {
    if (this.readByte() == NULL_VALUE) {
        return null
    }
    return this.readInt()
}