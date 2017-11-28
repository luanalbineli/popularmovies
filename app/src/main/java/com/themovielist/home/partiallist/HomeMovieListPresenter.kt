package com.themovielist.home.partiallist

import com.themovielist.base.BasePresenterImpl
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject

class HomeMovieListPresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeMovieListContract.Presenter {
    private lateinit var mView: HomeMovieListContract.View

    override fun setView(view: HomeMovieListContract.View) {
        mView = view
    }

    override fun showMovies(movieList: List<MovieModel>) {
        mView.showLoadingIndicator()
        mMovieRepository.getFavoriteMovieIds().subscribe({favoriteMovieIdArray ->
            val movieImageViewList = movieList.map { MovieImageViewModel(it, favoriteMovieIdArray.contains(it.id)) }
            mView.showMovies(movieImageViewList)
        }, { error ->
            mView.showErrorLoadingMovieList(error)
        })
    }
}
