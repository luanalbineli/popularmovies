package com.themovielist.repository.intheaters

import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.repository.movie.CommonRepository
import io.reactivex.Observable
import retrofit2.Retrofit
import javax.inject.Inject

class InTheatersRepository
@Inject
constructor(mRetrofit: Retrofit, private val mCommonRepository: CommonRepository) : RepositoryBase<IInTheatersMovieService>(mRetrofit) {

    fun getInTheatersMovieList(pageIndex: Int): Observable<PaginatedArrayResponseModel<MovieWithGenreModel>> {

        return mCommonRepository.getUserRegion()
                .flatMap { userRegion -> mApiInstance.getInTheatersMovieList(userRegion, pageIndex) }
                .flatMap({ mCommonRepository.getAllGenres() }, { inTheatersMovieList, genreMap ->
                    mCommonRepository.fillMovieGenresList(inTheatersMovieList, genreMap)
                })
    }

    override val getApiInstanceType: Class<IInTheatersMovieService>
        get() = IInTheatersMovieService::class.java
}