package com.themovielist.moviecast

import com.themovielist.model.MovieCastModel
import com.themovielist.model.response.ConfigurationResponseModel
import com.themovielist.model.response.MovieCreditsResponseModel
import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class MovieCastPresenter @Inject constructor(private val movieRepository: MovieRepository,
                                             private val commonRepository: CommonRepository) : MovieCastContract.Presenter {
    private lateinit var mView: MovieCastContract.View
    private lateinit var movieCastViewModel: MovieCastViewModel
    private var mRequest: Disposable? = null
    override fun setView(view: MovieCastContract.View) {
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

    override fun setMovieId(movieId: Int?) {
        this.movieCastViewModel.movieId = movieId
        if (movieId == null) {
            return
        }

        loadMovieCast(movieId)
    }

    private fun loadMovieCast(movieId: Int) {
        mView.showLoadingIndicator()
        val configurationRequest = commonRepository.getConfiguration()
        val castRequest = movieRepository.getMovieCreditsByMovieId(movieId)
        Observable.zip(
                configurationRequest,
                castRequest,
                BiFunction<ConfigurationResponseModel, MovieCreditsResponseModel, Pair<ConfigurationResponseModel, List<MovieCastModel>>> { t1, t2 ->
                    Pair(t1, t2.movieCastList)
                })
                .doOnTerminate { mView.hideLoadingIndicator() }
                .subscribe({ response ->
                    this.movieCastViewModel.profileSizeList = response.first.imageResponseModel.getProfileSizeList()
                    this.movieCastViewModel.movieCastList = response.second
                    mView.showMovieCastList(response.second, response.first.imageResponseModel.getProfileSizeList())
                }, { error -> mView.showErrorLoadingMovieCast(error) })
    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    override fun tryAgain() = loadMovieCast(movieCastViewModel.movieId!!)
}