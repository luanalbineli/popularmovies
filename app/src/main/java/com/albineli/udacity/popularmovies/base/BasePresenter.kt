package com.albineli.udacity.popularmovies.base


interface BasePresenter<in TView> {
    fun setView(view: TView)
}
