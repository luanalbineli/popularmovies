package com.themovielist.recommendation

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieListViewModel

interface MovieRecommendationContract {
    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
        fun showMovieRecommendationList(movieRecommendationList: List<MovieModel>)
        fun showErrorLoadingMovieCast(error: Throwable)
        fun showEmptyRecommendationListMessage()
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieListViewModel: MovieListViewModel)
        fun onStop()
        fun setMovieId(movieId: Int?)
        fun tryAgain()
    }
}