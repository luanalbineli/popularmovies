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
    private lateinit var mMovieDetailResponseModel: MovieDetailResponseModel
    private var isFavorite = false

    override fun start(movieModel: MovieModel) {
        mView.showLoadingMovieDetailIndicator()
        mCommonRepository.getAllGenres().subscribe({ genreMap ->
            val genreList = mCommonRepository.fillMovieGenresList(movieModel, genreMap)
            mMovieWithGenreModel = MovieWithGenreModel(movieModel, genreList)
            mView.showMovieInfo(mMovieWithGenreModel)

            fetchMovieDetailInfo()
        }, { error ->
            mView.showErrorLoadingMovieDetail(error)
        })

        mMovieRepository.isMovieFavorite(movieModel.id).subscribe({
            mView.setFavoriteButtonState(it)
            isFavorite = it
            Timber.i("IS MOVIE FAVORITE: $it")
        })
    }

    private fun fetchMovieDetailInfo() {
        mView.showLoadingMovieDetailIndicator()
        mMovieRepository.getMovieDetail(mMovieWithGenreModel.id)
                .subscribe(this::handleGetMovieDetailResponseSuccess, { error ->
                    mView.showErrorLoadingMovieDetail(error)
                })
    }

    private fun handleGetMovieDetailResponseSuccess(movieDetailResponseModel: MovieDetailResponseModel) {
        mMovieDetailResponseModel = movieDetailResponseModel
        mView.showMovieDetailInfo()

        mView.bindMovieReviewInfo(mMovieDetailResponseModel.reviewsResponseModel.results)
        mView.bindMovieTrailerInfo(mMovieDetailResponseModel.trailerResponseModel.trailerList)

        val hourMinute = convertRuntimeToHourMinute(movieDetailResponseModel.runtime)
        mView.showMovieRuntime(hourMinute)

        mView.hideLoadingMovieDetailIndicator()
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
        mView.showAllTrailers(mMovieDetailResponseModel.trailerResponseModel.trailerList)
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
                            mView.setFavoriteButtonState(isFavorite)
                            mView.dispatchFavoriteMovieEvent(mMovieWithGenreModel, isFavorite)
                        }
                ) { throwable ->
                    Timber.e(throwable, "An error occurred while tried to remove the favorite movie")
                    mView.showErrorMessageRemoveFavoriteMovie()
                }
    }

    override fun tryFecthMovieDetailAgain() = fetchMovieDetailInfo()
}
