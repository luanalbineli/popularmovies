package com.albineli.udacity.popularmovies.repository.movie


import com.albineli.udacity.popularmovies.model.MovieModel
import com.albineli.udacity.popularmovies.model.MovieReviewModel
import com.albineli.udacity.popularmovies.model.MovieTrailerModel
import com.albineli.udacity.popularmovies.repository.ArrayRequestAPI

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieService {
    @GET("movie/top_rated")
    fun getTopRatedList(@Query("page") pageNumber: Int?): Observable<ArrayRequestAPI<MovieModel>>

    @GET("movie/popular")
    fun getPopularList(@Query("page") pageNumber: Int?): Observable<ArrayRequestAPI<MovieModel>>

    @GET("movie/{movieId}/reviews")
    fun getReviewsByMovieId(@Path("movieId") movieId: Int, @Query("page") pageNumber: Int?): Observable<ArrayRequestAPI<MovieReviewModel>>

    @GET("movie/{movieId}/videos")
    fun getTrailersByMovieId(@Path("movieId") movieId: Int): Observable<ArrayRequestAPI<MovieTrailerModel>>
}
