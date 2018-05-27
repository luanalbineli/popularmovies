package com.themovielist.home

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.HomeMovieSortEnum
import com.themovielist.model.MovieModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.model.view.HomeViewModel
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class HomePresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), HomeContract.Presenter {
    private var mRequest: Disposable? = null
    private lateinit var mView: HomeContract.View

    lateinit var viewModel: HomeViewModel

    override fun setView(view: HomeContract.View) {
        mView = view
    }

    override fun start(viewModel: HomeViewModel) {
        this.viewModel = viewModel

        mView.showLoadingIndicator()

        if (viewModel.movieListByPopularity != null && viewModel.movieListByRating != null) {
            hideLoadingIndicatorAndShowMovies(viewModel.movieListByPopularity!!, viewModel.movieListByRating!!)
            return
        }

        fetchMovieListByPopularityAndRating()
    }

    private fun fetchMovieListByPopularityAndRating() {
        val popularRequest = mMovieRepository.getPopularList(ApiUtil.INITIAL_PAGE_INDEX)
        val topRatedRequest = mMovieRepository.getTopRatedList(ApiUtil.INITIAL_PAGE_INDEX)

        mRequest = Single.zip(popularRequest, topRatedRequest,
                BiFunction<PaginatedArrayResponseModel<MovieModel>,
                        PaginatedArrayResponseModel<MovieModel>,
                        Pair<List<MovieModel>, List<MovieModel>>>
                {t1, t2 ->
                    Pair(t1.results, t2.results)
                })
                .subscribe({ result ->
                    hideLoadingIndicatorAndShowMovies(result.first, result.second)
                    this.viewModel.movieListByPopularity = result.first
                    this.viewModel.movieListByRating = result.second
                }, { error -> mView.showErrorLoadingMovies(error) })
    }

    private fun hideLoadingIndicatorAndShowMovies(popularList: List<MovieModel>, topRatedList: List<MovieModel>) {
        mView.showPopularMovies(popularList)
        mView.showTopRatedMovies(topRatedList)
        mView.hideLoadingIndicatorAndShowMovies()
    }


    override fun tryToLoadMoviesAgain() = start(this.viewModel)

    fun seeAllPopularMovieList() {
        mView.seeAllMoviesSortedBy(HomeMovieSortEnum.POPULAR)
    }

    fun sellAllRatingMovieList() {
        mView.seeAllMoviesSortedBy(HomeMovieSortEnum.RATING)
    }

    override fun onStop() {
        mRequest?.also {
            if (it.isDisposed) {
                it.dispose()
            }
        }
    }

    override fun onFavoriteMovieEvent(movie: MovieModel, favorite: Boolean) {
     // TODO: Fill
    }
}
