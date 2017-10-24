package com.themovielist.repository.movie


import com.themovielist.PopularMovieApplication
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.repository.ArrayRequestAPI
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
import java.sql.SQLDataException
import javax.inject.Inject

class MovieRepository @Inject
internal constructor(private val mRetrofit: Retrofit, private val mApplicationContext: PopularMovieApplication) : RepositoryBase() {

    private val movieServiceInstance by lazy { mRetrofit.create(IMovieService::class.java) }

    fun getFavoriteList(): Observable<ArrayRequestAPI<MovieModel>> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<ArrayRequestAPI<MovieModel>> { emitter ->
            mApplicationContext.safeContentResolver(emitter) {
                val cursor = query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null)
                if (cursor == null) {
                    emitter.onError(SQLDataException("An internal error occurred."))
                    return@safeContentResolver
                }

                cursor.tryExecute({
                    val favoriteMovieModelList = cursor.toList {
                        MovieModel.fromCursor(cursor)
                    }

                    val arrayRequestAPI = ArrayRequestAPI<MovieModel>()
                    arrayRequestAPI.results = favoriteMovieModelList
                    arrayRequestAPI.totalPages = 1
                    arrayRequestAPI.page = 1

                    emitter.onNext(arrayRequestAPI)
                }, { error ->
                    emitter.onError(error)
                })
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun get(): Observable<Array<Int>> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<Array<Int>> { emitter ->
            mApplicationContext.safeContentResolver(emitter) {
                val cursor = query(MovieContract.MovieEntry.CONTENT_URI, arrayOf(MovieContract.MovieEntry._ID), null, null, null)
                if (cursor == null) {
                    emitter.onError(SQLDataException("An internal error occurred."))
                    return@safeContentResolver
                }

                cursor.tryExecute({
                    val favoriteMovieModelList = cursor.toArray {
                        getInt(getColumnIndex(MovieContract.MovieEntry._ID))
                    }
                    emitter.onNext(favoriteMovieModelList)
                }, { error ->
                    emitter.onError(error)
                })
            }
        }).subscribeOn(Schedulers.io()))
    }

    fun getTopRatedList(pageIndex: Int): Observable<ArrayRequestAPI<MovieModel>> {
        return observeOnMainThread(movieServiceInstance.getTopRatedList(pageIndex))
    }

    fun getPopularList(pageIndex: Int): Observable<ArrayRequestAPI<MovieModel>> {
        return observeOnMainThread(movieServiceInstance.getPopularList(pageIndex))
    }

    fun getReviewsByMovieId(pageIndex: Int, movieId: Int): Observable<ArrayRequestAPI<MovieReviewModel>> {
        return observeOnMainThread(movieServiceInstance.getReviewsByMovieId(movieId, pageIndex))
    }

    fun getTrailersByMovieId(movieId: Int): Observable<List<MovieTrailerModel>> {
        return observeOnMainThread(movieServiceInstance.getTrailersByMovieId(movieId).map { listArrayRequestAPI -> listArrayRequestAPI.results })
    }

    fun removeFavoriteMovie(movieModel: MovieModel): Completable {
        return observeOnMainThread(Completable.create { emitter ->
            val contentResolver = mApplicationContext.contentResolver
            if (contentResolver == null) {
                emitter.onError(RuntimeException("Cannot get the ContentResolver"))
                return@create
            }

            val numberOfRemovedItems = contentResolver.delete(MovieContract.MovieEntry.buildMovieWithId(movieModel.id), null, null)
            if (numberOfRemovedItems != 1) {
                emitter.onError(SQLDataException("An internal error occurred."))
                return@create
            }

            emitter.onComplete()
        }.subscribeOn(Schedulers.io()))
    }

    fun saveFavoriteMovie(movieModel: MovieModel): Completable {
        return observeOnMainThread(Completable.create { emitter ->
            val contentResolver = mApplicationContext.contentResolver
            if (contentResolver == null) {
                emitter.onError(RuntimeException("Cannot get the ContentResolver"))
                return@create
            }

            val uri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, movieModel.toContentValues())
            if (uri == null) {
                emitter.onError(SQLDataException("An internal error occurred."))
                return@create
            }

            emitter.onComplete()
        }.subscribeOn(Schedulers.io()))
    }

    fun getMovieDetailById(id: Int): Observable<MovieModel> {
        return observeOnMainThread(Observable.create(ObservableOnSubscribe<MovieModel> { emitter ->
            val contentResolver = mApplicationContext.contentResolver
            if (contentResolver == null) {
                emitter.onError(RuntimeException("Cannot get the ContentResolver"))
                return@ObservableOnSubscribe
            }

            val cursor = contentResolver.query(MovieContract.MovieEntry.buildMovieWithId(id), null, null, null, null)
            if (cursor == null) {
                emitter.onError(SQLDataException("An internal error occurred."))
                return@ObservableOnSubscribe
            }

            try {
                if (cursor.moveToNext()) {
                    emitter.onNext(MovieModel.fromCursor(cursor))
                }
            } catch (ex: Exception) {
                emitter.onError(ex)
            } finally {
                cursor.close()
            }
        }).subscribeOn(Schedulers.io()))
    }
}
