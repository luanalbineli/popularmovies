package com.themovielist.intheaters

import com.themovielist.base.BasePresenterImpl
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject

class InTheatersPresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), InTheatersContract.Presenter {
    private var mView: InTheatersContract.View? = null

    override fun setView(view: InTheatersContract.View) {
        mView = view
    }
}
