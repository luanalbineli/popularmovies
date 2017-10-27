package com.themovielist.util

fun <T> IntArray.mapToListNotNull(mapper: (Int) -> T?): List<T> {
    val list = mutableListOf<T>()
    this.forEach {
        val result = mapper.invoke(it)
        if (result != null) {
            list.add(result)
        }
    }
    return list
}