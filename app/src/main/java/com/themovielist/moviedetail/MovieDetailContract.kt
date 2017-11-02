package com.themovielist.moviedetail

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.MovieWithGenreModel

/**
 * Presenter of the Movie Detail Fragment.
 */

abstract class MovieDetailContract {
    interface View {
        fun showMovieInfo(movieWithGenreModel: MovieWithGenreModel)

        fun setFavoriteButtonState(favorite: Boolean)

        fun showSuccessMessageAddFavoriteMovie()

        fun showSuccessMessageRemoveFavoriteMovie()

        fun showErrorMessageAddFavoriteMovie()

        fun showErrorMessageRemoveFavoriteMovie()

        fun showAllReviews(movieReviewList: List<MovieReviewModel>, hasMore: Boolean)

        fun showAllTrailers(movieTrailerList: List<MovieTrailerModel>)

        fun setFavoriteButtonEnabled(enabled: Boolean)
        fun showLoadingMovieDetailIndicator()
        fun hideLoadingMovieDetailIndicator()
        fun showErrorLoadingMovieDetail(error: Throwable)
        fun showMovieDetailInfo()
        fun showMovieRuntime(hourMinute: Pair<Int, Int>)
        fun bindMovieReviewInfo(movieReviewList: List<MovieReviewModel>)
        fun dispatchFavoriteMovieEvent(movieModel: MovieModel, isFavorite: Boolean)
        fun bindMovieTrailerInfo(movieTrailerList: List<MovieTrailerModel>)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieModel: MovieModel)

        fun showAllReviews()

        fun showAllTrailers()
        fun tryFecthMovieDetailAgain()
    }
}
