package com.themovielist.repository.movie;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.themovielist.PopularMovieApplication;
import com.themovielist.model.MovieModel;
import com.themovielist.model.MovieReviewModel;
import com.themovielist.model.MovieTrailerModel;
import com.themovielist.repository.ArrayRequestAPI;
import com.themovielist.repository.RepositoryBase;
import com.themovielist.repository.data.MovieContract;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MovieRepository extends RepositoryBase {
    private static IMovieService mMovieService;

    private final Retrofit mRetrofit;
    private final PopularMovieApplication mApplicationContext;

    @Inject
    MovieRepository(Retrofit retrofit, PopularMovieApplication applicationContext) {
        mRetrofit = retrofit;
        mApplicationContext = applicationContext;
    }

    private IMovieService getMovieServiceInstance() {
        if (mMovieService == null) {
            mMovieService = mRetrofit.create(IMovieService.class);
        }
        return mMovieService;
    }

    public Observable<ArrayRequestAPI<MovieModel>> getTopRatedList(final int pageIndex) {
        return observeOnMainThread(getMovieServiceInstance().getTopRatedList(pageIndex));
    }

    public Observable<ArrayRequestAPI<MovieModel>> getPopularList(final int pageIndex) {
        return observeOnMainThread(getMovieServiceInstance().getPopularList(pageIndex));
    }

    public Observable<ArrayRequestAPI<MovieReviewModel>> getReviewsByMovieId(int pageIndex, int movieId) {
        return observeOnMainThread(getMovieServiceInstance().getReviewsByMovieId(movieId, pageIndex));
    }

    public Observable<List<MovieTrailerModel>> getTrailersByMovieId(int movieId) {
        return observeOnMainThread(getMovieServiceInstance().getTrailersByMovieId(movieId).map(listArrayRequestAPI -> listArrayRequestAPI.getResults()));
    }

    public Observable<ArrayRequestAPI<MovieModel>> getFavoriteList() {
        return observeOnMainThread(Observable.create((ObservableOnSubscribe<ArrayRequestAPI<MovieModel>>) emitter -> {
            final ContentResolver contentResolver = mApplicationContext.getContentResolver();
            if (contentResolver == null) {
                emitter.onError(new RuntimeException("Cannot get the ContentResolver"));
                return;
            }

            final Cursor cursor = contentResolver.query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                emitter.onError(new SQLDataException("An internal error occurred."));
                return;
            }

            List<MovieModel> favoriteMovieModelList = new ArrayList<>(cursor.getCount());
            try {
                while (cursor.moveToNext()) {
                    favoriteMovieModelList.add(MovieModel.Companion.fromCursor(cursor));
                }

                ArrayRequestAPI<MovieModel> arrayRequestAPI = new ArrayRequestAPI<>();
                arrayRequestAPI.setResults(favoriteMovieModelList);
                arrayRequestAPI.setTotalPages(1);
                arrayRequestAPI.setPage(1);


                emitter.onNext(arrayRequestAPI);
            } catch (Exception ex) {
                emitter.onError(ex);
            } finally {
                cursor.close();
            }
        }).subscribeOn(Schedulers.io()));
    }

    public Completable removeFavoriteMovie(final MovieModel movieModel) {
        return observeOnMainThread(Completable.create(emitter -> {
            final ContentResolver contentResolver = mApplicationContext.getContentResolver();
            if (contentResolver == null) {
                emitter.onError(new RuntimeException("Cannot get the ContentResolver"));
                return;
            }

            final int numberOfRemovedItems = contentResolver.delete(MovieContract.MovieEntry.buildMovieWithId(movieModel.getId()), null, null);
            if (numberOfRemovedItems != 1) {
                emitter.onError(new SQLDataException("An internal error occurred."));
                return;
            }

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()));
    }

    public Completable saveFavoriteMovie(final MovieModel movieModel) {
        return observeOnMainThread(Completable.create(emitter -> {
            final ContentResolver contentResolver = mApplicationContext.getContentResolver();
            if (contentResolver == null) {
                emitter.onError(new RuntimeException("Cannot get the ContentResolver"));
                return;
            }

            final Uri uri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, movieModel.toContentValues());
            if (uri == null) {
                emitter.onError(new SQLDataException("An internal error occurred."));
                return;
            }

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()));
    }

    public Observable<MovieModel> getMovieDetailById(final int id) {
        return observeOnMainThread(Observable.create((ObservableOnSubscribe<MovieModel>) emitter -> {
            final ContentResolver contentResolver = mApplicationContext.getContentResolver();
            if (contentResolver == null) {
                emitter.onError(new RuntimeException("Cannot get the ContentResolver"));
                return;
            }

            final Cursor cursor = contentResolver.query(MovieContract.MovieEntry.buildMovieWithId(id), null, null, null, null);
            if (cursor == null) {
                emitter.onError(new SQLDataException("An internal error occurred."));
                return;
            }

            try {
                if (cursor.moveToNext()) {
                    emitter.onNext(MovieModel.Companion.fromCursor(cursor));
                }
            } catch (Exception ex) {
                emitter.onError(ex);
            } finally {
                cursor.close();
            }
        }).subscribeOn(Schedulers.io()));
    }
}
