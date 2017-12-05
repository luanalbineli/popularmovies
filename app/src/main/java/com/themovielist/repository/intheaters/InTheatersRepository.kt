package com.themovielist.repository.intheaters

import com.themovielist.model.response.MovieListResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject

class InTheatersRepository
@Inject
constructor(retrofit: Retrofit, private val movieRepository: MovieRepository) : RepositoryBase<IInTheatersMovieService>(retrofit) {
    fun getInTheatersMovieList(pageIndex: Int): Single<MovieListResponseModel> {
        return movieRepository.getMoviesWithGenreAndConfiguration(mApiInstance.getInTheatersMovieList(pageIndex))
    }

    override val getApiInstanceType: Class<IInTheatersMovieService>
        get() = IInTheatersMovieService::class.java
}