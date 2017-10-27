package com.themovielist.intheaters

import com.themovielist.repository.intheaters.InTheatersRepository
import javax.inject.Inject

class InTheatersPresenter @Inject
internal constructor(private val mInTheatersRepository: InTheatersRepository) : InTheatersContract.Presenter {
    private lateinit var mView: InTheatersContract.View

    override fun setView(view: InTheatersContract.View) {
        mView = view
    }

    override fun start() {
        mInTheatersRepository.getInTheatersMovieList(1)
                .subscribe({ inTheaterMovieList ->
                    mView.showMainMovieDetail(inTheaterMovieList.results[0])
                    mView.showMovieList(inTheaterMovieList.results)
                }, { error ->
                    mView.showErrorLoadingMovies(error)
                })

    }
}
