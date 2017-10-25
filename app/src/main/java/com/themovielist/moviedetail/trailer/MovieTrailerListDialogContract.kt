package com.themovielist.moviedetail.trailer

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieTrailerModel

object MovieTrailerListDialogContract {

    interface View {
        fun showTrailersIntoList(movieReviewList: List<MovieTrailerModel>)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieTrailerList: List<MovieTrailerModel>)
    }
}
