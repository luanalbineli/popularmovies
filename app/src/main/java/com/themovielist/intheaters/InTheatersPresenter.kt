package com.themovielist.intheaters

import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.repository.intheaters.InTheatersRepository
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.util.ApiUtil
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class InTheatersPresenter @Inject
internal constructor(private val mInTheatersRepository: InTheatersRepository, private val commonRepository: CommonRepository) : InTheatersContract.Presenter {
    private lateinit var mView: InTheatersContract.View
    private var mRequest: Disposable? = null

    override fun setView(view: InTheatersContract.View) {
        mView = view
    }

    override fun start() {
        mView.showLoadingIndicator()
        mRequest = mInTheatersRepository.getInTheatersMovieList(ApiUtil.INITIAL_PAGE_INDEX)
                .doAfterTerminate {
                    mRequest = null
                    mView.hideLoadingIndicator()
                }
                .subscribe({ response ->
                    mView.showMainMovieDetail(response.movieWithGenreList.results.first())

                    val finalMovieList = response.movieWithGenreList.results.map {
                        val genreList = commonRepository.fillMovieGenresList(it.movieModel, response.genreListModel)
                        MovieImageGenreViewModel(genreList, it.movieModel, response.favoriteMovieIds.contains(it.movieModel.id))
                    }
                    mView.showMovieList(finalMovieList, response.configurationResponseModel.imageResponseModel)
                }, { error ->
                    mView.showErrorLoadingMovies(error)
                })
    }

    override fun onStop() {
        mRequest?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }
}
