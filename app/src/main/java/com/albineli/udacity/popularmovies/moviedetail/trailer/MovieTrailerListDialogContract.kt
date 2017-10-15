package com.albineli.udacity.popularmovies.moviedetail.trailer

import com.albineli.udacity.popularmovies.base.BasePresenter
import com.albineli.udacity.popularmovies.model.MovieTrailerModel

object MovieTrailerListDialogContract {

    interface View {
        fun showTrailersIntoList(movieReviewList: List<MovieTrailerModel>)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieReviewList: List<MovieTrailerModel>)
    }
}
