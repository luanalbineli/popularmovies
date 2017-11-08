package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.model.MovieCastModel

data class MovieCreditsResponseModel constructor(@SerializedName("cast") val movieCastList: List<MovieCastModel>)