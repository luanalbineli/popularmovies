package com.themovielist.movielist

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.MovieSortEnum
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Presenter of the movie list fragment.
 */

class MovieListPresenter @Inject
internal constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), MovieListContract.Presenter {
    private lateinit var mView: MovieListContract.View

    @MovieSortEnum.MovieSort
    internal var filter: Int = 0

    private var mHasError = false
    private var mSubscription: Disposable? = null
    /*
        The unitial page must be 1 (API implementation).
     */
    internal var pageIndex = 1
    internal var selectedMovieIndex = Integer.MIN_VALUE

    override fun setView(view: MovieListContract.View) {
        mView = view
    }

    override fun init(movieListStateModel: MovieListStateModel) {
        Timber.i("Movie list filter ${movieListStateModel.filter}")
        mView.setTitleByFilter(movieListStateModel.filter)

        filter = movieListStateModel.filter

        if (movieListStateModel.movieList == null) { // Handle a invalid state restore.
            loadMovieList(true)
            return
        }

        selectedMovieIndex = movieListStateModel.selectedMovieIndex
        Timber.i("Restoring the state - pageIndex: " + movieListStateModel.pageIndex +
                "\nselectedMovieIndex: " + movieListStateModel.selectedMovieIndex +
                "\nfirst visible item index: " + movieListStateModel.firstVisibleMovieIndex)

        handleSuccessLoadMovieList(movieListStateModel.movieList, true, true)

        if (movieListStateModel.firstVisibleMovieIndex != Integer.MIN_VALUE) {
            mView.scrollToMovieIndex(movieListStateModel.firstVisibleMovieIndex)
        }

        pageIndex = movieListStateModel.pageIndex
    }

    override fun onStop() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun loadMovieList(startOver: Boolean, @MovieSortEnum.MovieSort filter: Int) {
        Timber.i("loadMovieList - Loading the movie list")
        if (mSubscription != null) {
            return
        }

        mView.showLoadingIndicator()

        if (startOver) {
            pageIndex = 1
        } else {
            pageIndex++
        }

        val observable: Observable<PaginatedArrayResponseModel<MovieModel>> =
                when (filter) {
                    MovieSortEnum.POPULAR -> mMovieRepository.getPopularList(pageIndex)
                    MovieSortEnum.RATING -> mMovieRepository.getTopRatedList(pageIndex)
                    else -> mMovieRepository.getFavoriteList()
                }

        mSubscription = observable
                .doOnTerminate { mSubscription = null }
                .subscribe(
                        { response -> handleSuccessLoadMovieList(response.results, response.hasMorePages(), startOver) },
                        { throwable -> this.handleErrorLoadMovieList(throwable) })
    }

    private fun handleSuccessLoadMovieList(movieList: List<MovieModel>?, hasMorePages: Boolean, startOver: Boolean) {
        Timber.i("handleSuccessLoadMovieList - CHANGED")
        if (movieList!!.size == 0) {
            mView.clearMovieList() // Make sure that the list is empty.
            mView.showEmptyListMessage()
        } else {
            mView.showMovieList(movieList, startOver)
        }

        if (hasMorePages) {
            mView.enableLoadMoreListener()
        } else {
            mView.disableLoadMoreListener()
        }
    }

    private fun handleErrorLoadMovieList(throwable: Throwable) {
        mHasError = true
        Timber.e(throwable, "An error occurred while tried to get the movies")

        if (pageIndex > 1) { // If something got wrong, reverse to the original position.
            pageIndex--
        }

        if (filter == MovieSortEnum.FAVORITE) {
            mView.clearMovieList()
        }

        mView.showLoadingMovieListError()
    }

    override fun loadMovieList(startOver: Boolean) {
        loadMovieList(startOver, filter)
    }

    override fun changeFilterList(@MovieSortEnum.MovieSort movieListFilter: Int) {
        if (filter == movieListFilter) { // If it's the same order, do nothing.
            return
        }

        selectedMovieIndex = Integer.MIN_VALUE
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
            mSubscription = null
        }

        filter = movieListFilter
        mView.clearMovieList()

        // Reload the movie list.
        loadMovieList(true)

        mView.setTitleByFilter(movieListFilter)
    }

    override fun openMovieDetail(selectedMovieIndex: Int, movieModel: MovieModel) {
        this.selectedMovieIndex = selectedMovieIndex
        mView.showMovieDetail(movieModel)
    }

    override fun onListEndReached() {
        if (mHasError) {
            return
        }
        loadMovieList(false)
    }

    override fun tryAgain() {
        mHasError = false
        mView.hideRequestStatus()

        loadMovieList(false)
    }

    override fun favoriteMovie(movie: MovieModel, favorite: Boolean) {
        if (filter != MovieSortEnum.FAVORITE) { // Only if the user is at favorite list it needs an update.
            return
        }

        if (favorite) {
            mView.addMovieToListByIndex(selectedMovieIndex, movie)
        } else {
            mView.removeMovieFromListByIndex(selectedMovieIndex)
        }

        if (mView.movieListCount == 0) {
            mView.showEmptyListMessage()
        }
    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            mView.setTitleByFilter(filter)
        }
    }
}
