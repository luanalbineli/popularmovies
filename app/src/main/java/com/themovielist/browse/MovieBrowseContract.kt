package com.themovielist.browse

import com.themovielist.base.BasePresenter
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel

interface MovieBrowseContract {
    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
        fun showErrorLoadingQueryResult(error: Throwable)
        fun showMovieList(movieList: List<MovieImageGenreViewModel>, configurationImageResponseModel: ConfigurationImageResponseModel)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieCastViewModel: MovieCastViewModel)
        fun onStop()
        fun onQueryChanged(newQuery: String)
        fun tryAgain()
    }
}