package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.model.GenreModel

data class GenreListResponseModel constructor(@SerializedName("genres") val genreList: List<GenreModel>)
