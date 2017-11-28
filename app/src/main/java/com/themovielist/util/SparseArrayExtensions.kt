package com.themovielist.util

import android.util.SparseArray

fun <T> SparseArray<T>.values(): List<T> {
    return (0 until size())
            .map { keyAt(it) }
            .map { get(it) }
}

fun <T> SparseArray<T>.containsKey(key: Int): Boolean = this.indexOfKey(key) > 0