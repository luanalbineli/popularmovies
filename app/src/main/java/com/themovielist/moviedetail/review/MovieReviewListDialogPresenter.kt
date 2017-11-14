package com.themovielist.moviedetail.review

import com.themovielist.model.MovieReviewModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class MovieReviewListDialogPresenter @Inject
internal constructor(private val mMovieRepository: MovieRepository) : MovieReviewListDialogContract.Presenter {

    private lateinit var mView: MovieReviewListDialogContract.View

    private var mPageIndex: Int = 0
    private var mSubscription: Disposable? = null
    private var mMovieId: Int = 0

    override fun setView(view: MovieReviewListDialogContract.View) {
        mView = view
    }


    override fun start(movieReviewList: List<MovieReviewModel>, movieId: Int, hasMore: Boolean) {
        mView.addReviewsToList(movieReviewList)
        if (hasMore) {
            mView.enableLoadMoreListener()
        }

        mMovieId = movieId
    }

    override fun onListEndReached() {
        if (mSubscription != null) {
            return
        }

        mView.showLoadingIndicator()

        mPageIndex++

        val observable = mMovieRepository.getReviewsByMovieId(mPageIndex, mMovieId)

        mSubscription = observable.subscribe({ response ->
            this.handleSuccessLoadMovieReview(response)
        }, { error -> this.handleErrorLoadMovieReview(error) })
    }

    private fun handleSuccessLoadMovieReview(response: PaginatedArrayResponseModel<MovieReviewModel>) {
        mView.addReviewsToList(response.results)
        if (!response.hasMorePages()) {
            mView.disableLoadMoreListener()
        }
        mSubscription = null
    }

    private fun handleErrorLoadMovieReview(throwable: Throwable) {
        Timber.e(throwable, "An error occurred while tried to get the movie reviews for page: " + mPageIndex)
        mPageIndex--
        mView.showErrorLoadingReviews()
        mSubscription = null
    }

    override fun tryLoadReviewsAgain() {
        onListEndReached()
    }
}
