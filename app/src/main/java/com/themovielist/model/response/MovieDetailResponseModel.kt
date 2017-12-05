package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import java.util.*

class MovieDetailResponseModel(id: Int = 0, posterPath: String, overview: String,
                               title: String, voteAverage: Double, releaseDate: Date? = null,
                               backdropPath: String, genreIdList: IntArray) :
        MovieModel(id, posterPath, overview, title, voteAverage, releaseDate, backdropPath, genreIdList) {

    @SerializedName("reviews")
    lateinit var reviewsResponseModel: PaginatedArrayResponseModel<MovieReviewModel>

    @SerializedName("trailers")
    lateinit var trailerResponseModel: MovieDetailTrailerResponseModel

    @SerializedName("runtime")
    var runtime = 0
}