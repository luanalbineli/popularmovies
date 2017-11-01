package com.themovielist.moviedetail


import com.themovielist.base.BasePresenterImpl
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.MovieDetailResponseModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.repository.movie.MovieRepository
import timber.log.Timber
import javax.inject.Inject

class MovieDetailPresenter @Inject
internal constructor(movieRepository: MovieRepository, private val mCommonRepository: CommonRepository) : BasePresenterImpl(movieRepository), MovieDetailContract.Presenter {
    private lateinit var mView: MovieDetailContract.View
    private lateinit var mMovieWithGenreModel: MovieWithGenreModel
    private var isFavorite = false

    override fun start(movieModel: MovieModel) {
        mCommonRepository.getAllGenres().subscribe({ genreMap ->
            val genreList = mCommonRepository.fillMovieGenresList(movieModel, genreMap)
            mMovieWithGenreModel = MovieWithGenreModel(movieModel, genreList)
            mView.showMovieInfo(mMovieWithGenreModel)
        })

        mView.showLoadingMovieDetailIndicator()
        mMovieRepository.getMovieDetail(movieModel.id)
                .doOnTerminate { mView.hideLoadingMovieDetailIndicator() }
                .subscribe(this::handleGetMovieDetailResponseSuccess, { error ->
                    mView.showErrorLoadingMovieDetail(error)
                })

        mMovieRepository.isMovieFavorite(movieModel.id).subscribe({
            mView.setFavoriteButtonState(it)
            isFavorite = it
            Timber.i("IS MOVIE FAVORITE: $it")
        })
    }

    private lateinit var mMovieDetailResponseModel: MovieDetailResponseModel

    private fun handleGetMovieDetailResponseSuccess(movieDetailResponseModel: MovieDetailResponseModel) {
        mMovieDetailResponseModel = movieDetailResponseModel
        mView.showMovieDetailInfo()

        if (mMovieDetailResponseModel.reviewsResponseModel.results.isEmpty()) {
            mView.showMessageEmptyReview()
            mView.hideSeeAllReviewsButton()
        } else {
            if (mMovieDetailResponseModel.reviewsResponseModel.results.size > 1) {
                mView.showReadAllReviewsButton(mMovieDetailResponseModel.reviewsResponseModel.results.size)
            } else {
                mView.hideSeeAllReviewsButton()
            }
            mView.showFirstReviewInfo(mMovieDetailResponseModel.reviewsResponseModel.results[0])
        }

        val hourMinute = convertRuntimeToHourMinute(movieDetailResponseModel.runtime)
        mView.showMovieRuntime(hourMinute)
    }

    private fun convertRuntimeToHourMinute(runtime: Int): Pair<Int, Int> {
        val hours = runtime / 60
        val minutes = runtime % 60
        return Pair(hours, minutes)
    }


    override fun setView(view: MovieDetailContract.View) {
        mView = view
    }

    override fun showAllReviews() {
        mView.showAllReviews(mMovieDetailResponseModel.reviewsResponseModel.results, mMovieDetailResponseModel.reviewsResponseModel.hasMorePages())
    }

    override fun showAllTrailers() {
        mView.showAllTrailers(mMovieDetailResponseModel.trailersResponseModel.results)
    }

    fun toggleFavoriteMovie() {
        val request = if (isFavorite) {
            mMovieRepository.removeFavoriteMovie(mMovieWithGenreModel)
        } else {
            mMovieRepository.saveFavoriteMovie(mMovieWithGenreModel)
        }

        mView.setFavoriteButtonEnabled(false)
        request
                .doOnTerminate {
                    mView.setFavoriteButtonState(isFavorite)
                    mView.setFavoriteButtonEnabled(true)
                }
                .subscribe(
                        {
                            if (isFavorite) {
                                mView.showSuccessMessageRemoveFavoriteMovie()
                            } else {
                                mView.showSuccessMessageAddFavoriteMovie()
                            }
                            isFavorite = !isFavorite
                        }
                ) { throwable ->
                    Timber.e(throwable, "An error occurred while tried to remove the favorite movie")
                    mView.showErrorMessageRemoveFavoriteMovie()
                }
    }
}
