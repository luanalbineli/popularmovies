package com.themovielist.home

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.MovieSortEnum
import com.themovielist.model.MovieModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject

class HomePresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeContract.Presenter {
    private lateinit var mView: HomeContract.View

    override fun setView(view: HomeContract.View) {
        mView = view
    }

    override fun start() {
        mView.showLoadingIndicator()
        Observable.zip(mMovieRepository.getPopularList(DEFAULT_PAGE), mMovieRepository.getTopRatedList(DEFAULT_PAGE),
                BiFunction<PaginatedArrayResponseModel<MovieModel>,
                        PaginatedArrayResponseModel<MovieModel>,
                        Pair<List<MovieModel>, List<MovieModel>>> { t1, t2 -> Pair(t1.results, t2.results) })
                .subscribe({ result ->
                    mView.showPopularMovies(result.first)
                    mView.showTopRatedMovies(result.second)
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
