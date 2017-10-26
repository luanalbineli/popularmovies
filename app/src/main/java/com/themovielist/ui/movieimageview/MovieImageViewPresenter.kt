package com.themovielist.ui.movieimageview

import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject


class MovieImageViewPresenter @Inject constructor(private val mMovieRepository: MovieRepository) : MovieImageViewContract.Presenter {
    private lateinit var mView: MovieImageViewContract.View

    private lateinit var mMovieImageViewModel: MovieImageViewModel

    override fun setView(view: MovieImageViewContract.View) {
        mView = view
    }

    override fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel) {
        mMovieImageViewModel = movieImageViewModel
        mView.showMovieInfo(movieImageViewModel)
    }

    override fun toggleMovieFavorite() {
        mView.toggleMovieFavouriteEnabled(false)
        val request = if (mMovieImageViewModel.isFavourite) mMovieRepository.removeFavoriteMovie(mMovieImageViewModel.movieModel) else mMovieRepository.saveFavoriteMovie(mMovieImageViewModel.movieModel)
        request.doOnTerminate { mView.toggleMovieFavouriteEnabled(true) }
                .subscribe({
                    // Update the model.
                    mMovieImageViewModel.isFavourite = !mMovieImageViewModel.isFavourite
                }, { error ->
                    // Return the like button to the old state.
                    mView.toggleMovieFavorite(mMovieImageViewModel.isFavourite)
                    mView.showErrorFavoriteMovie(error)
                })
    }

    override fun showMovieDetail() {
        mView.openMovieDetail(mMovieImageViewModel.movieModel)

    }

    override fun onFavoriteMovieEvent(movie: MovieModel, favourite: Boolean) {
        if (mMovieImageViewModel.movieModel == movie && mMovieImageViewModel.isFavourite != favourite) {
            mMovieImageViewModel.isFavourite = favourite
            mView.toggleMovieFavouriteEnabled(favourite)
        }
    }
}