package com.themovielist.repository.movie

import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieWithGenreModel
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

    fun getAllGenres(): Observable<SparseArray<GenreModel>> {
        if (GENRE_MAP.size() > 0) { // TODO: Try to compare in a better way (null)
            return Observable.just(GENRE_MAP)
        }

        return observeOnMainThread(mApiInstance.getAllGenres()).map { result ->
            result.genreList.forEach { genreModel -> GENRE_MAP.put(genreModel.id, genreModel) }
            GENRE_MAP
        }
    }

    fun getUserRegion(): Observable<String> {
        return Observable.just(Locale.getDefault().country)
    }

    fun fillMovieGenresList(movieListResponseModel: PaginatedArrayResponseModel<MovieWithGenreModel>, genreMap: SparseArray<GenreModel>): PaginatedArrayResponseModel<MovieWithGenreModel> {
        movieListResponseModel.results.forEach { movieModel ->
            movieModel.genreList = movieModel.genreIdList.mapToListNotNull { genreId ->
                if (genreMap.indexOfKey(genreId) > -1) genreMap.get(genreId) else null
            }
        }

        return movieListResponseModel
    }

    override val getApiInstanceType: Class<ICommonMovieService>
        get() = ICommonMovieService::class.java

    companion object {
        @JvmField
        var GENRE_MAP = SparseArray<GenreModel>()
    }
}