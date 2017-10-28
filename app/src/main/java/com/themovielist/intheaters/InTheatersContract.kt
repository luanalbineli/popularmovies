package com.themovielist.intheaters

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieWithGenreModel

interface InTheatersContract {
    interface View {
        fun showMainMovieDetail(movieWithGenreModel: MovieWithGenreModel)
        fun showMovieList(results: List<MovieWithGenreModel>)
        fun showErrorLoadingMovies(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun start()
    }
}
