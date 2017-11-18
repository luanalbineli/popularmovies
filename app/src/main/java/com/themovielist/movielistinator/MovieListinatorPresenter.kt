package com.themovielist.movielistinator

import com.themovielist.base.BasePresenterImpl
import com.themovielist.enums.MovieSortEnum
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieListinatorViewModel
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class MovieListinatorPresenter @Inject constructor(movieRepository: MovieRepository) : BasePresenterImpl(movieRepository), MovieListinatorContract.Presenter {
    private lateinit var mView: MovieListinatorContract.View

    @MovieSortEnum.MovieSort
    internal var filter: Int = 0

    private var mHasError = false
    private var mSubscription: Disposable? = null

    internal var selectedMovieIndex = Integer.MIN_VALUE

    override fun setView(view: MovieListinatorContract.View) {
        mView = view
    }

    override fun init(movieListinatorViewModel: MovieListinatorViewModel) {
        if (movieListinatorViewModel.movieList == null) { // Handle a invalid state restore.
            return
        }

        if (movieListinatorViewModel.firstVisibleMovieIndex != Integer.MIN_VALUE) {
            mView.scrollToMovieIndex(movieListinatorViewModel.firstVisibleMovieIndex!!)
        }
    }

    override fun onStop() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


    override fun loadMovieList(startOver: Boolean) {
        loadMovieList(startOver)
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