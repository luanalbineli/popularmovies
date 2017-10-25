package com.themovielist.ui.movieimageview

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel


object MovieImageViewContract {
    interface View {
        fun openMovieDetail(movieModel: MovieModel)

        fun toggleMenuOpened(opened: Boolean)
        fun toggleMovieFavorite(favorite: Boolean)
        fun toggleMovieFavoriteEnabled(enabled: Boolean)
        fun showErrorFavoriteMovie(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {

        fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel)
        fun toggleMovieFavorite()
        fun showMovieDetail()
        fun openMenu()
        fun closeMenu()
    }
}