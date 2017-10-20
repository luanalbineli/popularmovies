package com.themovielist.home

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel
import com.themovielist.repository.ArrayRequestAPI

interface HomeContract {
    interface View {
        fun showPopularMovies(popularList: List<MovieModel>)
        fun showTopRatedMovies(popularList: List<MovieModel>)
        fun showErrorLoadingMovies(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun start()
    }
}
