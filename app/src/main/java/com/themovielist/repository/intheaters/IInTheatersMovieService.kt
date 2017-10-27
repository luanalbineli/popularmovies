package com.themovielist.repository.intheaters

import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface IInTheatersMovieService {
    @GET("movie/now_playing")
    fun getInTheatersMovieList(@Query("region") region: String, @Query("pageIndex") pageIndex: Int): Observable<PaginatedArrayResponseModel<MovieWithGenreModel>>
}