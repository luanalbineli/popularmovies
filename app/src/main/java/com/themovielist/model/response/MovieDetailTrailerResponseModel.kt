package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.model.MovieTrailerModel

data class MovieDetailTrailerResponseModel constructor(
        @SerializedName("youtube")
        val trailerList: List<MovieTrailerModel>)