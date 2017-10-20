package com.themovielist.home.list

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.MovieListFilterDescriptor
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel
import com.themovielist.repository.ArrayRequestAPI
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class HomeMovieListPresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeMovieListContract.Presenter {
    private lateinit var mView: HomeMovieListContract.View

    override fun setView(view: HomeMovieListContract.View) {
        mView = view
    }

    override fun showMovies(movieList: List<MovieModel>) {
        mView.showMovies(movieList)
    }
}
