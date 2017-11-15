package com.themovielist.browse

import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieSuggestionModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MovieBrowsePresenter @Inject constructor(private val movieRepository: MovieRepository,
                                             private val commonRepository: CommonRepository) : MovieBrowseContract.Presenter {
    private lateinit var mView: MovieBrowseContract.View
    private lateinit var movieCastViewModel: MovieCastViewModel
    private var mRequest: Disposable? = null
    private var mQueryRequest: Disposable? = null

    override fun setView(view: MovieBrowseContract.View) {
        mView = view
    }

    override fun start(movieCastViewModel: MovieCastViewModel) {
        this.movieCastViewModel = movieCastViewModel

        mView.showLoadingIndicator()
        movieCastViewModel.movieCastList?.let {
            mView.showMovieCastList(it, movieCastViewModel.profileSizeList!!)
        }
        mView.hideLoadingIndicator()
    }

    override fun onQueryChanged(newQuery: String?) {
        mView.showLoadingQueryResultIndicator()
        mQueryRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        if (newQuery == null || newQuery.length < 3) {
            mView.hideLoadingQueryResultIndicator()
            return
        }

        mView.showLoadingQueryResultIndicator()
        mQueryRequest = movieRepository.queryMovies(newQuery)
                .doAfterTerminate { mView.hideLoadingQueryResultIndicator() }
                .subscribe({ response ->
                    val suggestion = if (response.results.size > 10)
                        response.results.subList(0, 11)
                    else
                        response.results
            mView.showSuggestion(suggestion)
        })
    }

    override fun tryAgain() {
    }

    override fun onSelectSuggestion(movieSuggestionModel: MovieSuggestionModel) {
    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}