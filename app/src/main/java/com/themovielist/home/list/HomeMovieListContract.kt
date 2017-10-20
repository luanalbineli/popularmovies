package com.themovielist.home.list

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel

interface HomeMovieListContract {
    interface View {
        fun showMovies(movieList: List<MovieModel>)
    }

    interface Presenter : BasePresenter<View> {
        fun showMovies(movieList: List<MovieModel>)
    }
}
