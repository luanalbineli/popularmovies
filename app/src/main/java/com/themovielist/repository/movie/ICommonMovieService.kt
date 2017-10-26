package com.themovielist.repository.movie

import com.themovielist.model.GenreModel
import com.themovielist.repository.ArrayRequestAPI
import io.reactivex.Observable
import retrofit2.http.GET

interface ICommonMovieService {
    @GET("genre/movie/list")
    fun getAllGenres(): Observable<ArrayRequestAPI<GenreModel>>
}