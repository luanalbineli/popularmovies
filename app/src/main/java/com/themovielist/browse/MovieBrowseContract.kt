package com.themovielist.browse

import com.themovielist.base.BasePresenter
import com.themovielist.model.MovieCastModel
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieSizeModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.response.PaginatedArrayResponseModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.model.view.MovieSuggestionModel

interface MovieBrowseContract {
    interface View {
        fun showLoadingIndicator()
        fun hideLoadingIndicator()
        fun showErrorLoadingMovieCast(error: Throwable)
        fun showLoadingQueryResultIndicator()
        fun showSuggestion(suggestionList: List<MovieModel>)
        fun hideLoadingQueryResultIndicator()
        fun closeSuggestion()
        fun showMovieList(movieList: List<MovieImageGenreViewModel>, configurationImageResponseModel: ConfigurationImageResponseModel)
    }

    interface Presenter : BasePresenter<View> {
        fun start(movieCastViewModel: MovieCastViewModel)
        fun onStop()
        fun onQueryChanged(newQuery: String?)
        fun tryAgain()
        fun onSelectSuggestion(movieSuggestionModel: MovieSuggestionModel)
    }
}