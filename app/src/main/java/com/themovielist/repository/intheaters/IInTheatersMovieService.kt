package com.themovielist.repository.intheaters

import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface IInTheatersMovieService {
    @GET("movie/now_playing")
    fun getInTheatersMovieList(@Query("pageIndex") pageIndex: Int): Single<PaginatedArrayResponseModel<MovieModel>>
}