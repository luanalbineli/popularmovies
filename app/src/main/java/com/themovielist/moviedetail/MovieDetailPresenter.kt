package com.themovielist.moviedetail


import com.themovielist.base.BasePresenterImpl
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.movie.MovieRepository
import timber.log.Timber
import javax.inject.Inject

class MovieDetailPresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), MovieDetailContract.Presenter {
    private lateinit var mView: MovieDetailContract.View
    private var mMovieReviewRequest: PaginatedArrayResponseModel<MovieReviewModel>? = null
    private var mMovieTrailerList: List<MovieTrailerModel>? = null
    private var mMovieId: Int = 0

    override fun start(movieModel: MovieModel) {
        mView.showMovieDetail(movieModel)

        // If it is in the database, means that is favorite.
        mMovieRepository.isMovieFavourite(movieModel.id).subscribe({
            mView.setFavoriteButtonState(it)
        })

        loadMovieReviews(movieModel.id)

        loadMovieTrailers(movieModel.id)

        mMovieId = movieModel.id
    }

    private fun loadMovieTrailers(movieId: Int) {
        mView.showLoadingTrailersIndicator()
        mMovieRepository.getTrailersByMovieId(movieId).subscribe(
                { this.handleMovieTrailerRequestSuccess(it) }
        ) {
            Timber.e(it, "An error occurred while tried to get the movie trailers")
            mView.showErrorMessageLoadTrailers()
        }
    }

    private fun loadMovieReviews(movieId: Int) {
        mView.showLoadingReviewsIndicator()
        mMovieRepository.getReviewsByMovieId(1, movieId).subscribe(
                { this.handleMovieReviewRequestSuccess(it) }
        ) {
            Timber.e(it, "An error occurred while tried to get the movie reviews")
            mView.showErrorMessageLoadReviews()
        }
    }

    private fun handleMovieTrailerRequestSuccess(movieTrailerModels: List<MovieTrailerModel>) {
        if (movieTrailerModels.isEmpty()) {
            mView.showEmptyTrailerListMessage()
            mView.setShowAllTrailersButtonVisibility(false)
            return
        }

        mMovieTrailerList = movieTrailerModels
        if (mMovieTrailerList!!.size > 2) {
            mView.showMovieTrailer(mMovieTrailerList!!.subList(0, 2))
            mView.setShowAllTrailersButtonVisibility(true)
        } else {
            mView.showMovieTrailer(mMovieTrailerList!!)
            mView.setShowAllTrailersButtonVisibility(false)
        }
    }

    private fun handleMovieReviewRequestSuccess(movieReviewModelArrayRequestAPI: PaginatedArrayResponseModel<MovieReviewModel>) {
        if (movieReviewModelArrayRequestAPI.results.isEmpty()) {
            mView.showEmptyReviewListMessage()
            mView.setShowAllReviewsButtonVisibility(false)
            return
        }
        mMovieReviewRequest = movieReviewModelArrayRequestAPI
        if (movieReviewModelArrayRequestAPI.results.size > 2) {
            mView.showMovieReview(mMovieReviewRequest!!.results.subList(0, 2))
            mView.setShowAllReviewsButtonVisibility(true)
        } else {
            mView.showMovieReview(mMovieReviewRequest!!.results)
            mView.setShowAllReviewsButtonVisibility(false)
        }
    }

    override fun setView(view: MovieDetailContract.View) {
        mView = view
    }

    override fun removeFavoriteMovie(movieModel: MovieModel) {
        mMovieRepository.removeFavoriteMovie(movieModel).subscribe(
                { mView.showSuccessMessageRemoveFavoriteMovie() }
        ) { throwable ->
            Timber.e(throwable, "An error occurred while tried to remove the favorite movie")
            mView.showErrorMessageRemoveFavoriteMovie()
            mView.setFavoriteButtonState(true)
        }
    }

    override fun saveFavoriteMovie(movieModel: MovieModel) {
        mMovieRepository.saveFavoriteMovie(movieModel).subscribe(
                { mView.showSuccessMessageAddFavoriteMovie() }
        ) { throwable ->
            Timber.e(throwable, "An error occurred while tried to add the favorite movie")
            mView.showErrorMessageAddFavoriteMovie()
            mView.setFavoriteButtonState(false)
        }
    }

    override fun showAllReviews() {
        mView.showAllReviews(mMovieReviewRequest!!.results, mMovieReviewRequest!!.hasMorePages())
    }

    override fun showAllTrailers() {
        mView.showAllTrailers(mMovieTrailerList!!)
    }

    override fun tryToLoadTrailersAgain() {
        loadMovieTrailers(mMovieId)
    }

    override fun tryToLoadReviewAgain() {
        loadMovieReviews(mMovieId)
    }
}
