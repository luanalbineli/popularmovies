package com.themovielist.repository.movie


import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.response.MovieCreditsResponseModel
import com.themovielist.model.response.MovieDetailResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieService {
    @GET("movie/top_rated")
    fun getTopRatedList(@Query("page") pageNumber: Int?): Single<PaginatedArrayResponseModel<MovieModel>>

    @GET("movie/popular")
    fun getPopularList(@Query("page") pageNumber: Int?): Single<PaginatedArrayResponseModel<MovieModel>>

    @GET("movie/{movieId}/reviews")
    fun getReviewsByMovieId(@Path("movieId") movieId: Int, @Query("page") pageNumber: Int?): Observable<PaginatedArrayResponseModel<MovieReviewModel>>

    @GET("movie/{movieId}?append_to_response=reviews,trailers")
    fun getMovieDetail(@Path("movieId") movieId: Int): Observable<MovieDetailResponseModel>

    @GET("movie/{movieId}/credits")
    fun getMovieCredits(@Path("movieId") movieId: Int): Single<MovieCreditsResponseModel>

    @GET("search/movie")
    fun queryMovies(@Query("query") query: String): Single<PaginatedArrayResponseModel<MovieModel>>
}
