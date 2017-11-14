package com.themovielist.browse

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieCastModel
import com.themovielist.model.MovieSizeModel
import com.themovielist.model.view.MovieCastViewModel

interface MovieBrowseContract {
    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
        fun showMovieCastList(movieCastList: List<MovieCastModel>, profileSizeList: List<MovieSizeModel>)
        fun showErrorLoadingMovieCast(error: Throwable)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieCastViewModel: MovieCastViewModel)
        fun onStop()
        fun onQueryChanged(newQuery: String?)
        fun tryAgain()
    }
}