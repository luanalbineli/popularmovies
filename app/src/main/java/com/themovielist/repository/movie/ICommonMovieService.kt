package com.themovielist.repository.movie

import com.themovielist.model.response.GenreListResponseModel
import io.reactivex.Observable
import retrofit2.http.GET

interface ICommonMovieService {
    @GET("genre/movie/list")
    fun getAllGenres(): Observable<GenreListResponseModel>
}