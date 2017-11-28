package com.themovielist.home.fulllist

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.model.view.HomeFullMovieListViewModel


object HomeFullMovieListContract {
    interface View {
        fun showLoadingUpcomingMoviesIndicator()
        fun showErrorLoadingUpcomingMovies(error: Throwable)
        fun addUpcomingMovieList(upcomingMovieList: List<MovieWithGenreModel>, configurationResponseModel: ConfigurationImageResponseModel)
        fun hideLoadingIndicator()
        fun showMovieDetail(movieWithGenreModel: MovieWithGenreModel)
        fun enableLoadMoreListener()
        fun disableLoadMoreListener()
        fun showEmptyListMessage()
        fun scrollToItemPosition(firstVisibleItemPosition: Int)
        fun showGenreList(genreListItemList: List<GenreListItemModel>)
        fun replaceUpcomingMovieList(finalUpcomingMovieList: List<MovieWithGenreModel>, imageResponseModel: ConfigurationImageResponseModel)
    }

    interface Presenter: BasePresenter<View> {
        fun start(upcomingMoviesViewModel: HomeFullMovieListViewModel?)
        fun tryAgain()
        fun showMovieDetail(movieWithGenreModel: MovieWithGenreModel)
        fun onStop(firstVisibleItemPosition: Int)
        fun loadMoreMovies()
        fun onChangeSelectedGenre(genreListItemModel: GenreListItemModel)
    }
}