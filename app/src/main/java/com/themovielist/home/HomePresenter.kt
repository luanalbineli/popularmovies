package com.themovielist.home

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.MovieSortEnum
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject

class HomePresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeContract.Presenter {
    private lateinit var mView: HomeContract.View

    override fun setView(view: HomeContract.View) {
        mView = view
    }

    override fun start() {
        mView.showLoadingIndicator()
        mMovieRepository.getPopularList(DEFAULT_PAGE)
                .flatMap(
                        { mMovieRepository.getTopRatedList(DEFAULT_PAGE) },
                        { popularMovieList, topRatedMovieList -> Pair(popularMovieList, topRatedMovieList) })
                .subscribe({ result ->
                    mView.showPopularMovies(result.first.results)
                    mView.showTopRatedMovies(result.second.results)
                    mView.hideLoadingIndicatorAndShowMovies()
                }, { error -> mView.showErrorLoadingMovies(error) })
    }

    override fun tryToLoadMoviesAgain() = start()

    fun seeAllPopularMovieList() {
        mView.seeAllMoviesSortedBy(MovieSortEnum.POPULAR)

    }

    fun sellAllRatingMovieList() {
        mView.seeAllMoviesSortedBy(MovieSortEnum.RATING)
    }

    companion object {
        const val DEFAULT_PAGE = 1 // The api page is non zero based index
    }
}
