package com.themovielist.util

fun <T> IntArray.mapToListNotNull(mapper: (Int) -> T?): List<T>? {
    var list:  MutableList<T>? = null
    this.forEach {
        val result = mapper.invoke(it)
        if (result != null) {
            if (list == null) {
                list = mutableListOf()
            }
            list?.add(result)
        }
    }
    return list
}