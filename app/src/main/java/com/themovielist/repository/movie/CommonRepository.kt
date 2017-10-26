package com.themovielist.repository.movie

import android.util.SparseArray
import com.themovielist.repository.RepositoryBase
import io.reactivex.Observable
import retrofit2.Retrofit
import javax.inject.Inject

class CommonRepository
@Inject
constructor(retrofit: Retrofit) : RepositoryBase<ICommonMovieService>(retrofit) {

    fun getAllGenres(): Observable<SparseArray<String>> {
        if (GENRE_MAP.size() > 0) { // TODO: Try to compare in a better way (null)
            return Observable.just(GENRE_MAP)
        }

        return observeOnMainThread(mApiInstance.getAllGenres()).map { result ->
            result.results.forEach { genreModel -> GENRE_MAP.put(genreModel.id, genreModel.name) }
            GENRE_MAP
        }
    }

    override val getApiInstanceType: Class<ICommonMovieService>
        get() = ICommonMovieService::class.java

    companion object {
        @JvmField
        var GENRE_MAP = SparseArray<String>()
    }
}