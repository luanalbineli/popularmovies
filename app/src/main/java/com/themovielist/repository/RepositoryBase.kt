package com.themovielist.repository


import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit

abstract class RepositoryBase<T> constructor(private val retrofit: Retrofit) {
    abstract val getApiInstanceType: Class<T>

    protected val mApiInstance: T by lazy { retrofit.create(getApiInstanceType) }

    protected fun <T> observeOnMainThread(observable: Observable<T>): Observable<T> {
        return observable.observeOn(AndroidSchedulers.mainThread())
    }

    protected fun observeOnMainThread(completable: Completable): Completable {
        return completable.observeOn(AndroidSchedulers.mainThread())
    }
}
