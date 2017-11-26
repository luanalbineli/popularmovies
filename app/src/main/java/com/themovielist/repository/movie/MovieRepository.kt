package com.themovielist.repository.movie


import com.themovielist.PopularMovieApplication
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.response.MovieCreditsResponseModel
import com.themovielist.model.response.MovieDetailResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.repository.data.MovieContract
import com.themovielist.util.toArray
import com.themovielist.util.toList
import com.themovielist.util.tryExecute
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import java.sql.SQLDataException
import javax.inject.Inject

class MovieRepository @Inject
internal constructor(mRetrofit: Retrofit, private val mApplicationContext: PopularMovieApplication) : RepositoryBase<IMovieService>(mRetrofit) {

    fun getFavoriteList(): Single<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(Single.create(SingleOnSubscribe<PaginatedArrayResponseModel<MovieModel>> { emitter ->
            mApplicationContext.safeContentResolver(emitter) {
                val cursor = query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null)
                if (cursor == null) {
                    emitter.onError(SQLDataException("An internal error occurred."))
                    return@safeContentResolver
                }

                cursor.tryExecute(emitter) {
                    val favoriteMovieModelList = cursor.toList {
                        MovieModel.fromCursor(cursor)
                    }

                    val arrayRequestAPI = PaginatedArrayResponseModel<MovieModel>()
                    arrayRequestAPI.results = favoriteMovieModelList
                    arrayRequestAPI.totalPages = 1
                    arrayRequestAPI.page = 1

                    emitter.onSuccess(arrayRequestAPI)
                }
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun getFavoriteMovieIds(): Single<Array<Int>> {
        return observeOnMainThread(Single.create(SingleOnSubscribe<Array<Int>> { emitter ->
            mApplicationContext.safeContentResolver(emitter) {
                val cursor = query(MovieContract.MovieEntry.CONTENT_URI, arrayOf(MovieContract.MovieEntry._ID), null, null, null)
                if (cursor == null) {
                    emitter.onError(SQLDataException("An internal error occurred."))
                    return@safeContentResolver
                }

                cursor.tryExecute(emitter) {
                    val favoriteMovieModelList = cursor.toArray {
                        getInt(getColumnIndex(MovieContract.MovieEntry._ID))
                    }
                    emitter.onSuccess(favoriteMovieModelList)
                }
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun getTopRatedList(pageIndex: Int): Single<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(mApiInstance.getTopRatedList(pageIndex))
    }

    fun getPopularList(pageIndex: Int): Single<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(mApiInstance.getPopularList(pageIndex))
    }

    fun getReviewsByMovieId(pageIndex: Int, movieId: Int): Observable<PaginatedArrayResponseModel<MovieReviewModel>> {
        return observeOnMainThread(mApiInstance.getReviewsByMovieId(movieId, pageIndex))
    }

    fun removeFavoriteMovie(movieModel: MovieModel): Completable {
        return observeOnMainThread(Completable.create { emitter ->
            Timber.d("Trying to remove movie from favorite: $movieModel")
            val contentResolver = mApplicationContext.contentResolver
            if (contentResolver == null) {
                emitter.onError(RuntimeException("Cannot get the ContentResolver"))
                return@create
            }

            val numberOfRemovedItems = contentResolver.delete(MovieContract.MovieEntry.buildMovieWithId(movieModel.id), null, null)
            Timber.d("Number of removed movies: $numberOfRemovedItems")
            if (numberOfRemovedItems != 1) {
                emitter.onError(SQLDataException("An internal error occurred."))
                return@create
            }

            emitter.onComplete()
        }.subscribeOn(Schedulers.io()))
    }

    fun saveFavoriteMovie(movieModel: MovieModel): Completable {
        return observeOnMainThread(Completable.create { emitter ->
            Timber.d("Trying to set movie as favorite: $movieModel")
            val contentResolver = mApplicationContext.contentResolver
            if (contentResolver == null) {
                emitter.onError(RuntimeException("Cannot get the ContentResolver"))
                return@create
            }

            val uri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, movieModel.toContentValues())
            Timber.d("Result of the insertion: $uri")
            if (uri == null) {
                emitter.onError(SQLDataException("An internal error occurred."))
                return@create
            }

            emitter.onComplete()
        }.subscribeOn(Schedulers.io()))
    }

    fun isMovieFavorite(movieId: Int): Single<Boolean> {
        return observeOnMainThread(Single.create(SingleOnSubscribe<Boolean> { emitter ->
            mApplicationContext.tryQueryOnContentResolver(emitter, {
                query(MovieContract.MovieEntry.buildMovieWithId(movieId), null, null, null, null)
            }, {
                emitter.onSuccess(moveToNext())
            })
        }).subscribeOn(Schedulers.io()))
    }

    fun getMovieCreditsByMovieId(movieId: Int): Single<MovieCreditsResponseModel> {
        return observeOnMainThread(mApiInstance.getMovieCredits(movieId))
    }

    override val getApiInstanceType: Class<IMovieService>
        get() = IMovieService::class.java

    fun getMovieDetail(movieId: Int): Observable<MovieDetailResponseModel> {
        return observeOnMainThread(mApiInstance.getMovieDetail(movieId))
    }

    fun queryMovies(newQuery: String): Single<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(mApiInstance.queryMovies(newQuery))
    }
}
