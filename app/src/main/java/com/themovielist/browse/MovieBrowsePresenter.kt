package com.themovielist.browse

import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import com.themovielist.util.ApiUtil
import io.reactivex.disposables.Disposable
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

    override fun onQueryChanged(newQuery: String) {
        this.newQuery = newQuery

        loadMovieResultByQuery(newQuery)
    }

    override fun tryAgain() {
        loadMovieResultByQuery(newQuery)
    }

    private fun loadMovieResultByQuery(newQuery: String) {
        mView.showLoadingIndicator()
        mRequest = movieRepository.getFavoriteMovieListWithGenreAndConfiguration(newQuery, ApiUtil.INITIAL_PAGE_INDEX)
                .doAfterTerminate {
                    mView.hideLoadingIndicator()
                    mRequest = null
                }
                .subscribe({ response ->
                    val finalMovieList = response.movieWithGenreList.results.map {
                        val genreList = commonRepository.fillMovieGenresList(it.movieModel, response.genreListModel)
                        MovieImageGenreViewModel(genreList, it.movieModel, response.favoriteMovieIds.contains(it.movieModel.id))
                    }
                    /*viewModel.movieList.addAll(finalMovieList)
                    viewModel.imageResponseModel = response.configurationResponseModel.imageResponseModel*/
                    mView.showMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel)
                }, { error -> mView.showErrorLoadingQueryResult(error) }
                )
    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}