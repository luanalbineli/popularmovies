package com.themovielist.repository

import android.util.LruCache

class Cache private constructor() {
    val lru: LruCache<Any, Any> = LruCache(1024)

    companion object {
        @JvmField
        val instance = Cache()
    }
}