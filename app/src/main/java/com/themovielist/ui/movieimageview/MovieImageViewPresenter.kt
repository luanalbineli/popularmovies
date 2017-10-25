package com.themovielist.ui.movieimageview

import com.themovielist.model.MovieImageViewModel
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject


class MovieImageViewPresenter @Inject constructor(val mMovieRepository: MovieRepository): MovieImageViewContract.Presenter {
    private lateinit var mView: MovieImageViewContract.View

    private lateinit var mMovieImageViewModel: MovieImageViewModel

    override fun setView(view: MovieImageViewContract.View) {
        mView = view
    }

    override fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel) {
        mMovieImageViewModel = movieImageViewModel

        mView.toggleMenuOpened(mMovieImageViewModel.isMenuOpen)
        mView.toggleMovieFavorite(mMovieImageViewModel.isFavorite)
    }

    override fun toggleMovieFavorite() {
        mView.toggleMovieFavoriteEnabled(false)
        mMovieRepository.saveFavoriteMovie(mMovieImageViewModel.movieModel)
                .doOnTerminate { mView.toggleMovieFavoriteEnabled(true) }
                .subscribe({
                    // Update the model.
                    mMovieImageViewModel.isFavorite = !mMovieImageViewModel.isFavorite
                }, { error ->
                    // Return the like button to the old state.
                    mView.toggleMovieFavorite(mMovieImageViewModel.isFavorite)
                    mView.showErrorFavoriteMovie(error)
                })
    }

    override fun openMenu() {
        mMovieImageViewModel.isMenuOpen = true
        mView.toggleMenuOpened(mMovieImageViewModel.isMenuOpen)
    }

    override fun showMovieDetail() {
        mView.openMovieDetail(mMovieImageViewModel.movieModel)

    }

    override fun closeMenu() {
        mMovieImageViewModel.isMenuOpen = false
        mView.toggleMenuOpened(mMovieImageViewModel.isMenuOpen)
    }
}