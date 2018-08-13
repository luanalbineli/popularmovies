package com.themovielist.recommendation

import com.themovielist.model.view.MovieListViewModel
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MovieRecommendationPresenter @Inject constructor(private val movieRepository: MovieRepository) : MovieRecommendationContract.Presenter {
    private lateinit var mView: MovieRecommendationContract.View
    private lateinit var movieListViewModel: MovieListViewModel
    private var mRequest: Disposable? = null
    private var mMovieId: Int? = null

    override fun setView(view: MovieRecommendationContract.View) {
        mView = view
    }

    override fun start(movieListViewModel: MovieListViewModel) {
        this.movieListViewModel = movieListViewModel

        mView.showLoadingIndicator()
        movieListViewModel.movieList?.let {
            mView.showMovieRecommendationList(it)
        }
        mView.hideLoadingIndicator()
    }

    override fun setMovieId(movieId: Int?) {
        this.mMovieId = movieId
        if (movieId == null) {
            return
        }

        loadMovieCast(movieId)
    }

    private fun loadMovieCast(movieId: Int) {
        mView.showLoadingIndicator()
        mRequest = movieRepository.getMovieRecommendationsByMovieId(movieId)
                .doAfterTerminate { mView.hideLoadingIndicator() }
                .subscribe({ response ->
                    this.movieListViewModel.movieList = response.results
                    if (response.results.isEmpty()) {
                        mView.showEmptyRecommendationListMessage()
                    } else {
                        mView.showMovieRecommendationList(response.results)
                    }
                }, { error -> mView.showErrorLoadingMovieCast(error) })

    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    override fun tryAgain() {
        mMovieId?.let {
            loadMovieCast(it)
        }
    }
}