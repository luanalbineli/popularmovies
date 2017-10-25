package com.themovielist.moviedetail.trailer

import com.themovielist.model.MovieTrailerModel

import javax.inject.Inject

class MovieTrailerListDialogPresenter @Inject
constructor() : MovieTrailerListDialogContract.Presenter {
    private var mView: MovieTrailerListDialogContract.View? = null

    override fun setView(view: MovieTrailerListDialogContract.View) {
        mView = view
    }


    override fun start(movieTrailerList: List<MovieTrailerModel>) {
        mView!!.showTrailersIntoList(movieTrailerList)
    }
}
