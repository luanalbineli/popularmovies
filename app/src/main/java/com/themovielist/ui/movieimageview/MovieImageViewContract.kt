package com.themovielist.ui.movieimageview

import com.themovielist.base.BasePresenter
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.model.MovieModel


object MovieImageViewContract {
    interface View {
        fun openMovieDetail(movieModel: MovieModel)

        fun toggleMovieFavorite(favourite: Boolean)
        fun toggleMovieFavouriteEnabled(enabled: Boolean)
        fun showErrorFavoriteMovie(error: Throwable)
        fun showMovieInfo(movieImageViewModel: MovieImageViewModel)
    }

    interface Presenter: BasePresenter<View> {

        fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel)
        fun toggleMovieFavorite()
        fun showMovieDetail()
        fun onFavoriteMovieEvent(movie: MovieModel, favourite: Boolean)
    }
}