package com.themovielist.browse

import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.model.view.MovieSuggestionModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import com.themovielist.util.Defaults
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class MovieBrowsePresenter @Inject constructor(private val movieRepository: MovieRepository,
                                             private val commonRepository: CommonRepository) : MovieBrowseContract.Presenter {
    private lateinit var mView: MovieBrowseContract.View
    private lateinit var movieCastViewModel: MovieCastViewModel
    private var mRequest: Disposable? = null

    private var newQuery = ""

    override fun setView(view: MovieBrowseContract.View) {
        mView = view
    }

    override fun start(movieCastViewModel: MovieCastViewModel) {
        this.movieCastViewModel = movieCastViewModel

        /*mView.showLoadingIndicator()
        movieCastViewModel.movieCastList?.let {
            mView.showMovieCastList(it, movieCastViewModel.profileSizeList!!)
        }
        mView.hideLoadingIndicator()*/
    }

    override fun onQueryChanged(newQuery: String?) {
        mView.showLoadingQueryResultIndicator()
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        if (newQuery == null || newQuery.length < 3) {
            mView.hideLoadingQueryResultIndicator()
            return
        }

        mView.showLoadingQueryResultIndicator()
        mRequest = movieRepository.queryMovies(newQuery, ApiUtil.INITIAL_PAGE_INDEX)
                .doAfterTerminate {
                    mView.hideLoadingQueryResultIndicator()
                    mRequest = null
                }
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
        Timber.d("onSelectSuggestion - movieSuggestionModel: $movieSuggestionModel")
        mView.closeSuggestion()

        newQuery = movieSuggestionModel.body

        mView.showLoadingIndicator()
        mRequest = movieRepository.getFavoriteMovieListWithGenreAndConfiguration(newQuery, ApiUtil.INITIAL_PAGE_INDEX)
                .doAfterTerminate {
                    mView.hideLoadingIndicator()
                    mRequest = null
                }
                .subscribe { response ->
                    val finalMovieList = response.upcomingMovieList.results.map {
                        val genreList = commonRepository.fillMovieGenresList(it.movieModel, response.genreListModel)
                        MovieImageGenreViewModel(genreList, it.movieModel, response.favoriteMovieIds.contains(it.movieModel.id))
                    }
                    /*viewModel.movieList.addAll(finalMovieList)
                    viewModel.imageResponseModel = response.configurationResponseModel.imageResponseModel*/
                    mView.showMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel)
                }

    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}