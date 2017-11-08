package com.themovielist.moviecast

import com.themovielist.model.view.MovieCastViewModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import javax.inject.Inject

class MovieCastPresenter @Inject constructor(private val movieRepository: MovieRepository,
                                             private val commonRepository: CommonRepository) : MovieCastContract.Presenter {
    private lateinit var mView: MovieCastContract.View

    override fun setView(view: MovieCastContract.View) {
        mView = view
    }

    override fun start(movieCastViewModel: MovieCastViewModel) {
        mView.showLoadingIndicator()
        val configurationRequest = commonRepository.getConfiguration()
        val castRequest = movieRepository.getMovieCreditsByMovieId(movieCastViewModel.movieId)
    }
}