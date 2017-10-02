package com.albineli.udacity.popularmovies.repository;


import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class RepositoryBase {
    protected <T> Observable<T> observeOnMainThread(Observable<T> observable) {
        return observable.observeOn(AndroidSchedulers.mainThread());
    }

    protected  Completable observeOnMainThread(Completable completable) {
        return completable.observeOn(AndroidSchedulers.mainThread());
    }
}
