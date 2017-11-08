package com.themovielist.repository.movie

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.util.mapToListNotNull
import io.reactivex.Observable
import retrofit2.Retrofit
import java.util.*
import javax.inject.Inject

class CommonRepository
@Inject
constructor(retrofit: Retrofit) : RepositoryBase<ICommonMovieService>(retrofit) {
    private var mGetAllGenresRequest: Observable<SparseArray<GenreModel>>? = null

    private var mConfigurationRequest: Observable<ConfigurationResponseModel>? = null
    private var mConfigurationResponseModel: ConfigurationResponseModel? = null

    @Synchronized
    fun getAllGenres(): Observable<SparseArray<GenreModel>> {
        if (GENRE_MAP != null) {
            Observable.just(GENRE_MAP!!)
        }

        return mGetAllGenresRequest ?: observeOnMainThread(mApiInstance.getAllGenres()).map { result ->
            SparseArray<GenreModel>().also { sparseArray ->
                result.genreList.forEach { genreModel -> sparseArray.put(genreModel.id, genreModel) }
                GENRE_MAP = sparseArray
                mGetAllGenresRequest = null
            }
        }.also {
            mGetAllGenresRequest = it
        }
    }

    @Synchronized
    fun getConfiguration(): Observable<ConfigurationResponseModel> {
        if (mConfigurationResponseModel != null) {
            return Observable.just(mConfigurationResponseModel!!)
        }

        return mConfigurationRequest ?: observeOnMainThread(mApiInstance.getConfiguration()).also {
            mConfigurationRequest = it
            it.subscribe({response -> mConfigurationResponseModel = response})
        }
    }

    fun getUserRegion(): Observable<String> {
        return Observable.just(Locale.getDefault().country)
    }

    fun fillMovieGenresList(movieListResponseModel: PaginatedArrayResponseModel<MovieWithGenreModel>, genreMap: SparseArray<GenreModel>): PaginatedArrayResponseModel<MovieWithGenreModel> {
        movieListResponseModel.results.forEach { movieModel ->
            movieModel.genreList = fillMovieGenresList(movieModel, genreMap)
        }

        return movieListResponseModel
    }

    fun fillMovieGenresList(movieModel: MovieModel, genreMap: SparseArray<GenreModel>): List<GenreModel> {
        return movieModel.genreIdList.mapToListNotNull { genreId ->
            if (genreMap.indexOfKey(genreId) > -1) genreMap.get(genreId) else null
        }
    }

    override val getApiInstanceType: Class<ICommonMovieService>
        get() = ICommonMovieService::class.java

    companion object {
        @JvmField
        var GENRE_MAP: SparseArray<GenreModel>? = null
    }
}