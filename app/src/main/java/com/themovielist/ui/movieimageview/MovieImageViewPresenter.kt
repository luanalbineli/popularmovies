package com.themovielist.ui.movieimageview

import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.repository.movie.MovieRepository
import timber.log.Timber
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
        Timber.d("Toggling the movie favorite state: ${mMovieImageViewModel.movieModel}")
        mView.toggleMovieFavouriteEnabled(false)
        val request = if (mMovieImageViewModel.isFavorite) mMovieRepository.removeFavoriteMovie(mMovieImageViewModel.movieModel) else mMovieRepository.saveFavoriteMovie(mMovieImageViewModel.movieModel)
        request.doOnTerminate { mView.toggleMovieFavouriteEnabled(true) }
                .subscribe({
                    // Update the model.
                    mMovieImageViewModel.isFavorite = !mMovieImageViewModel.isFavorite
                }, { error ->
                    // Return the like button to the old state.
                    mView.toggleMovieFavoriteWithoutChangeEvent(mMovieImageViewModel.isFavorite)
                    mView.showErrorFavoriteMovie(error)
                })
    }

    override fun showMovieDetail() {
        mView.openMovieDetail(mMovieImageViewModel.movieModel)

    }

    override fun onFavoriteMovieEvent(movie: MovieModel, favourite: Boolean) {
        if (mMovieImageViewModel.movieModel == movie && mMovieImageViewModel.isFavorite != favourite) {
            mMovieImageViewModel.isFavorite = favourite
            mView.toggleMovieFavoriteWithoutChangeEvent(favourite)
        }
    }
}