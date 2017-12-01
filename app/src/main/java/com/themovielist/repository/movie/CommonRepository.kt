package com.themovielist.repository.movie

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.util.mapToListNotNull
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject

class CommonRepository
@Inject
constructor(retrofit: Retrofit) : RepositoryBase<ICommonMovieService>(retrofit) {
    private var mGetAllGenresRequest: Single<SparseArray<GenreModel>>? = null

    private var mConfigurationRequest: Single<ConfigurationResponseModel>? = null
    private var mConfigurationResponseModel: ConfigurationResponseModel? = null

    @Synchronized
    fun getAllGenres(): Single<SparseArray<GenreModel>> {
        return GENRE_MAP?.let {
            Single.just(it)
        } ?: {
            mGetAllGenresRequest ?: observeOnMainThread(mApiInstance.getAllGenres()).map { result ->
                SparseArray<GenreModel>().also { sparseArray ->
                    result.genreList.forEach { genreModel -> sparseArray.put(genreModel.id, genreModel) }
                    GENRE_MAP = sparseArray
                    result.genreList.forEachIndexed { index, genreModel ->
                        GENRE_COLOR_MAP.append(genreModel.id, COLORS[index])
                    }
                    mGetAllGenresRequest = null
                }
            }.also {
                mGetAllGenresRequest = it
            }
        }()
    }

    @Synchronized
    fun getConfiguration(): Single<ConfigurationResponseModel> {
        return mConfigurationResponseModel?.let {
            Single.just(it)
        } ?: {
            mConfigurationRequest ?: observeOnMainThread(mApiInstance.getConfiguration()).also {
                mConfigurationRequest = it
                it.subscribe({ response -> mConfigurationResponseModel = response })
            }
        }()
    }

    fun fillMovieGenresList(movieListResponseModel: PaginatedArrayResponseModel<MovieModel>, genreMap: SparseArray<GenreModel>): PaginatedArrayResponseModel<MovieWithGenreModel> {
        return PaginatedArrayResponseModel<MovieWithGenreModel>().also {
            it.results = movieListResponseModel.results.map {
                MovieWithGenreModel(it, fillMovieGenresList(it, genreMap))
            }

            it.page = movieListResponseModel.page
            it.totalPages = movieListResponseModel.totalPages
        }
    }

    fun fillMovieGenresList(movieModel: MovieModel, genreMap: SparseArray<GenreModel>): List<GenreModel>? {
        return movieModel.genreIdList.mapToListNotNull { genreId ->
            if (genreMap.indexOfKey(genreId) > -1) genreMap.get(genreId) else null
        }
    }

    override val getApiInstanceType: Class<ICommonMovieService>
        get() = ICommonMovieService::class.java

    companion object {
        @JvmField
        var GENRE_MAP: SparseArray<GenreModel>? = null

        @JvmField
        var GENRE_COLOR_MAP = SparseArray<String>()

        @JvmField
        var COLORS = arrayOf("#F44336", "#EC407A", "#AB47BC", "#4527A0",
                "#263238", "#000000", "#DD2C00", "#5D4037",
                "#757575", "#E65100", "#1B5E20", "#827717",
                "#0091EA", "#006064", "#6200EA", "#004D40",
                "#212121", "#FF1744", "#AA00FF", "#33691E",
                "#A1887F", "#C51162")
    }
}