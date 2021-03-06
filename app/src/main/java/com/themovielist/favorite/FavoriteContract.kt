package com.themovielist.favorite

import com.themovielist.base.BasePresenter
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel
import com.themovielist.model.view.MovieImageGenreViewModel


object FavoriteContract {
    interface View {
        fun showLoadingFavoriteMoviesIndicator()
        fun showErrorLoadingUpcomingMovies(error: Throwable)
        fun addMoviesToList(movieList: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel)
        fun hideLoadingIndicator()
        fun showMovieDetail(movieImageGenreViewModel: MovieImageGenreViewModel)
        fun enableLoadMoreListener()
        fun disableLoadMoreListener()
        fun showEmptyListMessage()
        fun scrollToItemPosition(firstVisibleItemPosition: Int)
        fun replaceMovieList(movieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel)
        fun setListViewType(useListViewType: Boolean)
        fun openDialogToSelectListSort(selectedSort: Int)
    }

    interface Presenter: BasePresenter<View> {
        fun start(upcomingMoviesViewModel: HomeFullMovieListViewModel?)
        fun tryAgain()
        fun showMovieDetail(movieImageGenreViewModel: MovieImageGenreViewModel)
        fun onStop(firstVisibleItemPosition: Int)
        fun loadMoreMovies()
        fun onChangeSelectedGenre(genreListItemModel: GenreListItemModel)
        fun onChangeListViewType(useListViewType: Boolean)
        fun handleSortMenuClick()
    }
}