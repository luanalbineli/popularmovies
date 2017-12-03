package com.themovielist.home

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel
import com.themovielist.model.view.HomeViewModel

interface HomeContract {
    interface View {
        fun showPopularMovies(popularList: List<MovieModel>)
        fun showTopRatedMovies(topRatedList: List<MovieModel>)
        fun showErrorLoadingMovies(error: Throwable)
        fun showLoadingIndicator()
        fun hideLoadingIndicatorAndShowMovies()
        fun seeAllMoviesSortedBy(homeMovieSort: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun start(viewModel: HomeViewModel)
        fun tryToLoadMoviesAgain()
        fun onFavoriteMovieEvent(movie: MovieModel, favorite: Boolean)
    }
}
