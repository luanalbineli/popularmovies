package com.themovielist.model

import com.google.gson.annotations.SerializedName

data class GenreModel(@SerializedName("id") val id: Int, @SerializedName("name") val name: String)