package com.themovielist.movielistinator

import com.themovielist.base.BasePresenter
import com.themovielist.enums.MovieSortEnum
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieListinatorViewModel


interface MovieListinatorContract {
    interface View {

        val movieListCount: Int
        fun setTitleByFilter(@MovieSortEnum.MovieSort filter: Int)

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
        fun init(movieListinatorViewModel: MovieListinatorViewModel)
        fun onStop()
        fun loadMovieList(startOver: Boolean)
        fun changeFilterList(@MovieSortEnum.MovieSort filter: Int)

        fun openMovieDetail(selectedMovieIndex: Int, movieModel: MovieModel)

        fun onListEndReached()

        fun tryAgain()

        fun favoriteMovie(movie: MovieModel, favorite: Boolean)

        fun onVisibilityChanged(visible: Boolean)
    }
}