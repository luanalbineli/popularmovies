package com.themovielist.repository.movie


import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.response.PaginatedArrayResponseModel

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieService {
    @GET("movie/top_rated")
    fun getTopRatedList(@Query("page") pageNumber: Int?): Observable<PaginatedArrayResponseModel<MovieModel>>

    @GET("movie/popular")
    fun getPopularList(@Query("page") pageNumber: Int?): Observable<PaginatedArrayResponseModel<MovieModel>>

    @GET("movie/{movieId}/reviews")
    fun getReviewsByMovieId(@Path("movieId") movieId: Int, @Query("page") pageNumber: Int?): Observable<PaginatedArrayResponseModel<MovieReviewModel>>

    @GET("movie/{movieId}/videos")
    fun getTrailersByMovieId(@Path("movieId") movieId: Int): Observable<PaginatedArrayResponseModel<MovieTrailerModel>>
}
