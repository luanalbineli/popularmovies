package com.themovielist.home

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel

interface HomeContract {
    interface View {
        fun showPopularMovies(popularList: List<MovieModel>)
        fun showTopRatedMovies(popularList: List<MovieModel>)
        fun showErrorLoadingMovies(error: Throwable)
        fun showLoadingIndicator()
        fun hideLoadingIndicatorAndShowMovies()
    }

    interface Presenter : BasePresenter<View> {
        fun start()
        fun tryToLoadMoviesAgain()
    }
}
