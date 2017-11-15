package com.themovielist.home.list

import com.themovielist.base.BasePresenter
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.model.MovieModel

interface HomeMovieListContract {
    interface View {
        fun showMovies(movieImageViewList: List<MovieImageViewModel>)
        fun showLoadingIndicator()
        fun showErrorLoadingMovieList(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun showMovies(movieList: List<MovieModel>)
    }
}
