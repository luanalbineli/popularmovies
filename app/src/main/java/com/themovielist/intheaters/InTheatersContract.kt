package com.themovielist.intheaters

import com.themovielist.base.BasePresenter
import com.themovielist.enums.MovieListFilterDescriptor
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel
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
