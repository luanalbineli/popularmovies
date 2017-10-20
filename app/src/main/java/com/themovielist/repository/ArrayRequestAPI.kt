package com.themovielist.repository

import com.google.gson.annotations.SerializedName

class ArrayRequestAPI<T> {
    @SerializedName("results")
    lateinit var results: List<T>

    @SerializedName("page")
    var page: Int = 0

    @SerializedName("total_pages")
    var totalPages: Int = 0

    fun hasMorePages(): Boolean {
        return page < totalPages
    }
}
