package com.themovielist.intheaters

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel

interface InTheatersContract {
    interface View {
        fun showMainMovieDetail(movieWithGenreModel: MovieWithGenreModel)
        fun showMovieList(results: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel)
        fun showErrorLoadingMovies(error: Throwable)
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
    }

    interface Presenter : BasePresenter<View> {
        fun start()
        fun onStop()
    }
}
