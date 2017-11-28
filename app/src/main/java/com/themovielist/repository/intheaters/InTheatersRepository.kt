package com.themovielist.repository.intheaters

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.repository.movie.CommonRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Retrofit
import javax.inject.Inject

class InTheatersRepository
@Inject
constructor(mRetrofit: Retrofit, private val mCommonRepository: CommonRepository) : RepositoryBase<IInTheatersMovieService>(mRetrofit) {

    fun getInTheatersMovieList(pageIndex: Int): Single<PaginatedArrayResponseModel<MovieWithGenreModel>> {

        val inTheatersMovieListRequest = mApiInstance.getInTheatersMovieList(pageIndex)
        val genreMapRequest = mCommonRepository.getAllGenres()
        return Single.zip(
                inTheatersMovieListRequest,
                genreMapRequest,
                BiFunction<PaginatedArrayResponseModel<MovieModel>, SparseArray<GenreModel>, PaginatedArrayResponseModel<MovieWithGenreModel>> { t1, t2 ->
                    mCommonRepository.fillMovieGenresList(t1, t2)
                })
    }

    override val getApiInstanceType: Class<IInTheatersMovieService>
        get() = IInTheatersMovieService::class.java
}