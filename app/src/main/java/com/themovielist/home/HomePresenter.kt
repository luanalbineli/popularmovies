package com.themovielist.home

import com.themovielist.base.BasePresenterImpl
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject

class HomePresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeContract.Presenter {
    private lateinit var mView: HomeContract.View

    override fun setView(view: HomeContract.View) {
        mView = view
    }

    override fun start() {
        val mPopularMovieCall = mMovieRepository.getPopularList(DEFAULT_PAGE)
        val mTopRatedMovieCall = mMovieRepository.getTopRatedList(DEFAULT_PAGE)
        mPopularMovieCall
                .flatMap({ mTopRatedMovieCall }, { popularMovieList, topRatedMovieList -> Pair(popularMovieList, topRatedMovieList) })
                .subscribe({ result ->
                    mView.showPopularMovies(result.first.results)
                    mView.showTopRatedMovies(result.second.results)
                }, { error -> mView.showErrorLoadingMovies(error) })
    }

    companion object {
        const val DEFAULT_PAGE = 1 // The api page is non zero based index
    }
}
