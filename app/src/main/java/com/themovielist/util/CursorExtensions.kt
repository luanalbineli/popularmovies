package com.themovielist.util

import android.database.Cursor
import io.reactivex.SingleEmitter

inline fun <T> Cursor.tryExecute(emitter: SingleEmitter<T>, invoker: Cursor.() -> Unit) {
    try {
        invoker.invoke(this)
    } catch (ex: Exception) {
        emitter.onError(ex)
    } finally {
        this.close()
    }
}

inline fun <T> Cursor.toList(invoker: Cursor.() -> T): ArrayList<T> {
    val list = ArrayList<T>(this.count)
    while (this.moveToNext()) {
        list.add(invoker.invoke(this))
    }
    return list
}

inline fun <reified T> Cursor.toArray(invoker: Cursor.() -> T): Array<T> {
    return Array(this.count, { _ ->
        this.moveToNext()
        invoker.invoke(this)
    })
}

fun Cursor.getIntArray(columnName: String): IntArray {
    val columnIndex = this.getColumnIndex(columnName)
    val stringContent = this.getString(columnIndex)
    return stringToIntArray(stringContent)
}

fun Cursor.getNullableString(columnName: String): String? {
    val columnIndex = this.getColumnIndex(columnName)
    if (this.isNull(columnIndex)) {
        return null
    }
    return this.getString(columnIndex)
}
