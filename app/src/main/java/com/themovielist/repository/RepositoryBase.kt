package com.themovielist.repository


import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class RepositoryBase {
    protected fun <T> observeOnMainThread(observable: Observable<T>): Observable<T> {
        return observable.observeOn(AndroidSchedulers.mainThread())
    }

    protected fun observeOnMainThread(completable: Completable): Completable {
        return completable.observeOn(AndroidSchedulers.mainThread())
    }
}
