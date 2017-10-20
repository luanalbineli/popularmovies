package com.themovielist.moviedetail.review

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieReviewModel

object MovieReviewListDialogContract {

    interface View {
        fun addReviewsToList(movieReviewList: List<MovieReviewModel>)

        fun enableLoadMoreListener()

        fun disableLoadMoreListener()

        fun showLoadingIndicator()

        fun showErrorLoadingReviews()
    }

    internal interface Presenter : BasePresenter<View> {
        fun start(movieReviewList: List<MovieReviewModel>, movieId: Int, hasMore: Boolean)

        fun onListEndReached()

        fun tryLoadReviewsAgain()
    }
}
