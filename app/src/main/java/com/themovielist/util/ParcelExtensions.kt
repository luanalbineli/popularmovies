package com.themovielist.util

import android.os.Parcel

fun Parcel.writeIntArrayWithLength(intArray: IntArray) {
    this.writeInt(intArray.size)
    this.writeIntArray(intArray)
}

fun Parcel.readIntArray(): IntArray {
    val intArray = IntArray(this.readInt())
    this.readIntArray(intArray)
    return intArray
}
