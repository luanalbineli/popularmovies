package com.themovielist.util

import android.database.Cursor
import io.reactivex.ObservableEmitter

inline fun <T> Cursor.tryExecute(emitter: ObservableEmitter<T>, invoker: Cursor.() -> Unit) {
    try {
        invoker.invoke(this)
    } catch (ex: Exception) {
        emitter.onError(ex)
    } finally {
        this.close()
    }
}

inline fun Cursor.tryExecute(invoker: Cursor.() -> Unit, onError: ((Exception) -> Unit)) {
    try {
        invoker.invoke(this)
    } catch (ex: Exception) {
        onError.invoke(ex)
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
