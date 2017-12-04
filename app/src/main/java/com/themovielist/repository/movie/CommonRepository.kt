package com.themovielist.repository.movie

import android.content.Context
import android.util.SparseArray
import com.themovielist.PopularMovieApplication
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.util.mapToListNotNull
import io.reactivex.Single
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject

class CommonRepository
@Inject
constructor(retrofit: Retrofit, var applicationContext: PopularMovieApplication) : RepositoryBase<ICommonMovieService>(retrofit) {
    private var mGetAllGenresRequest: Single<SparseArray<GenreModel>>? = null

    private var mConfigurationRequest: Single<ConfigurationResponseModel>? = null
    private var mConfigurationResponseModel: ConfigurationResponseModel? = null

    @Synchronized
    fun getAllGenres(): Single<SparseArray<GenreModel>> {
        Timber.d("Getting the genre map")
        return GENRE_MAP?.let {
            Timber.d("The map is cached")
            Single.just(it)
        } ?: {
            Timber.d("The map is not cached. There is a request for it? ${mGetAllGenresRequest != null}")
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
        Timber.d("Getting the configuration model")
        return mConfigurationResponseModel?.let {
            Timber.d("The configuration model is cached")
            Single.just(it)
        } ?: {
            Timber.d("The configuration model is no cached. And the request? ${mConfigurationRequest != null}")
            mConfigurationRequest ?: observeOnMainThread(mApiInstance.getConfiguration()).also {
                mConfigurationRequest = it
                it.subscribe({ response ->
                    mConfigurationResponseModel = response
                    mConfigurationRequest = null
                })
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

    fun getUseListViewType(defaultValue: Boolean) =
        applicationContext
                .getSharedPreferences(SP_COMMON_REPOSITORY, Context.MODE_PRIVATE)
                .getBoolean(SP_USE_LIST_VIEW_TYPE_KEY, defaultValue)

    fun putUseListViewType(useListViewType: Boolean) {
        applicationContext
                .getSharedPreferences(SP_COMMON_REPOSITORY, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SP_USE_LIST_VIEW_TYPE_KEY, useListViewType)
                .apply()
    }

    fun getFavoriteMovieSort(defaultFavoriteMovieSort: Int) =
            applicationContext
                    .getSharedPreferences(SP_COMMON_REPOSITORY, Context.MODE_PRIVATE)
                    .getInt(SP_FAVORITE_MOVIE_SORT_KEY, defaultFavoriteMovieSort)

    fun putFavoriteMovieSort(favoriteMovieSort: Int) {
        applicationContext
                .getSharedPreferences(SP_COMMON_REPOSITORY, Context.MODE_PRIVATE)
                .edit()
                .putInt(SP_FAVORITE_MOVIE_SORT_KEY, favoriteMovieSort)
                .apply()
    }

    override val getApiInstanceType: Class<ICommonMovieService>
        get() = ICommonMovieService::class.java

    companion object {

        const val SP_COMMON_REPOSITORY = "sp_common"

        const val SP_USE_LIST_VIEW_TYPE_KEY = "sp_use_list_view_type"
        const val SP_FAVORITE_MOVIE_SORT_KEY = "sp_favorite_movie_sort"

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