package com.themovielist.repository.movie


import com.themovielist.PopularMovieApplication
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.RepositoryBase
import com.themovielist.repository.data.MovieContract
import com.themovielist.util.toArray
import com.themovielist.util.toList
import com.themovielist.util.tryExecute
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import java.sql.SQLDataException
import javax.inject.Inject

class MovieRepository @Inject
internal constructor(mRetrofit: Retrofit, private val mApplicationContext: PopularMovieApplication) : RepositoryBase<IMovieService>(mRetrofit) {

    fun getFavoriteList(): Observable<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<PaginatedArrayResponseModel<MovieModel>> { emitter ->
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

                    emitter.onNext(arrayRequestAPI)
                }
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun getFavoriteMovieIds(): Observable<Array<Int>> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<Array<Int>> { emitter ->
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
                    emitter.onNext(favoriteMovieModelList)
                }
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun getTopRatedList(pageIndex: Int): Observable<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(mApiInstance.getTopRatedList(pageIndex))
    }

    fun getPopularList(pageIndex: Int): Observable<PaginatedArrayResponseModel<MovieModel>> {
        return observeOnMainThread(mApiInstance.getPopularList(pageIndex))
    }

    fun getReviewsByMovieId(pageIndex: Int, movieId: Int): Observable<PaginatedArrayResponseModel<MovieReviewModel>> {
        return observeOnMainThread(mApiInstance.getReviewsByMovieId(movieId, pageIndex))
    }

    fun getTrailersByMovieId(movieId: Int): Observable<List<MovieTrailerModel>> {
        return observeOnMainThread(mApiInstance.getTrailersByMovieId(movieId).map { listArrayRequestAPI -> listArrayRequestAPI.results })
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

    fun getMovieDetailById(id: Int): Observable<MovieModel> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<MovieModel> { emitter ->
            mApplicationContext.tryQueryOnContentResolver(emitter, {
                query(MovieContract.MovieEntry.buildMovieWithId(id), null, null, null, null)
            }, {
                val movieModel = if (moveToNext()) MovieModel.fromCursor(this) else MovieModel.EMPTY_MOVIE
                emitter.onNext(movieModel)
            })
        }).subscribeOn(Schedulers.io()))
    }

    fun isMovieFavourite(id: Int): Observable<Boolean> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<Boolean> { emitter ->
            mApplicationContext.tryQueryOnContentResolver(emitter, {
                query(MovieContract.MovieEntry.buildMovieWithId(id), null, null, null, null)
            }, {
                emitter.onNext(moveToNext())
            })
        }).subscribeOn(Schedulers.io()))
    }

    override val getApiInstanceType: Class<IMovieService>
        get() = IMovieService::class.java
}
