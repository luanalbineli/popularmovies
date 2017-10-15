package com.albineli.udacity.popularmovies.movielist

import com.albineli.udacity.popularmovies.base.BasePresenter
import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor
import com.albineli.udacity.popularmovies.model.MovieListStateModel
import com.albineli.udacity.popularmovies.model.MovieModel

interface MovieListContract {
    interface View {

        val movieListCount: Int
        fun setTitleByFilter(@MovieListFilterDescriptor.MovieListFilter filter: Int)

        fun showLoadingMovieListError()
        fun showMovieList(movieList: List<MovieModel>, replaceData: Boolean)
        fun showMovieDetail(movieModel: MovieModel)

        fun clearMovieList()

        fun showEmptyListMessage()
        fun hideRequestStatus()

        fun showLoadingIndicator()

        fun enableLoadMoreListener()

        fun disableLoadMoreListener()

        fun removeMovieFromListByIndex(index: Int)

        fun addMovieToListByIndex(index: Int, movieModel: MovieModel)

        fun scrollToMovieIndex(firstVisibleMovieIndex: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun init(movieListStateModel: MovieListStateModel)
        fun onStop()
        fun loadMovieList(startOver: Boolean)
        fun changeFilterList(@MovieListFilterDescriptor.MovieListFilter filter: Int)

        fun openMovieDetail(selectedMovieIndex: Int, movieModel: MovieModel)

        fun onListEndReached()

        fun tryAgain()

        fun favoriteMovie(movie: MovieModel, favorite: Boolean)

        fun onVisibilityChanged(visible: Boolean)
    }
}
