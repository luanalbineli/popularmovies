package com.themovielist.moviedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.content.res.AppCompatResources
import android.view.MenuItem
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseDaggerActivity
import com.themovielist.base.BasePresenter
import com.themovielist.enums.RequestStatusDescriptor
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.util.*
import kotlinx.android.synthetic.main.movie_detail_activity.*
import kotlinx.android.synthetic.main.movie_header_detail.*
import kotlinx.android.synthetic.main.movie_review_item.view.*
import timber.log.Timber
import java.security.InvalidParameterException
import java.util.*
import javax.inject.Inject


class MovieDetailActivity : BaseDaggerActivity<MovieDetailContract.View>(), MovieDetailContract.View {

    override val presenterImplementation: BasePresenter<MovieDetailContract.View>
        get() = mPresenter

    override val viewImplementation: MovieDetailContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieDetailPresenter

    private lateinit var mMovieModel: MovieModel

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null || !intent.hasExtra(MOVIE_KEY)) {
            throw InvalidParameterException("movie")
        }

        mMovieModel = intent.getParcelableExtra(MOVIE_KEY)

        setContentView(R.layout.movie_detail_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configureToolbar()

        tvMovieDetailReadAllReviews.setOnClickListener { mPresenter.showAllReviews() }
        tvMovieDetailSeeAllTrailers.setOnClickListener { mPresenter.showAllTrailers() }

        mPresenter.start(mMovieModel)
    }

    private var mFavoriteMenuItem: MenuItem? = null

    private fun configureToolbar() {
        configureToolbarBackButton(this, toolbarMovieDetail) {
            onBackPressed()
        }

        menuInflater.inflate(R.menu.movie_detail, toolbarMovieDetail.menu)
        mFavoriteMenuItem = toolbarMovieDetail.menu.findItem(R.id.movie_detail_favorite)
        mFavoriteMenuItem?.setOnMenuItemClickListener {
            mPresenter.toggleFavoriteMovie()
            true
        }
    }

    override fun showMovieInfo(movieWithGenreModel: MovieWithGenreModel) {
        val backdropWidth = ApiUtil.getDefaultPosterSize(UIUtil.getDisplayMetrics(sdvMovieHeaderBackdrop.context).widthPixels)
        val backdropUrl = ApiUtil.buildPosterImageUrl(movieWithGenreModel.backdropPath, backdropWidth)
        sdvMovieHeaderBackdrop.setImageURI(backdropUrl)

        tvMovieHeaderMovieName.text = movieWithGenreModel.title
        tvMovieHeaderMovieGenres.text = movieWithGenreModel.concattedGenres()
        Timber.i("RELEASE DATE: ${movieWithGenreModel.releaseDate} | YEAR: ${movieWithGenreModel.releaseDate?.yearFromCalendar}")
        tvMovieHeaderReleaseDate.text = movieWithGenreModel.releaseDate?.yearFromCalendar.toString()

        tvMovieDetailSynopsys.text = movieWithGenreModel.overview

        tvMovieDetailRating.text = movieWithGenreModel.voteAverage.toString()
    }

    override fun showMovieDetailInfo() {
        movieDetailContainer.setDisplay(true)
    }

    override fun setFavoriteButtonState(favorite: Boolean) {
        val favoriteIcon = AppCompatResources.getDrawable(this, if (favorite) R.drawable.heart else R.drawable.heart_outline)
        mFavoriteMenuItem?.icon = favoriteIcon
    }

    override fun showSuccessMessageAddFavoriteMovie() {
        showToastMessage(R.string.success_add_favorite_movie)
    }

    override fun showSuccessMessageRemoveFavoriteMovie() {
        showToastMessage(R.string.success_remove_favorite_movie)
    }

    override fun showErrorMessageAddFavoriteMovie() {
        showToastMessage(R.string.error_add_favorite_movie)
    }

    override fun showErrorMessageRemoveFavoriteMovie() {
        showToastMessage(R.string.error_remove_favorite_movie)
    }

    override fun showAllReviews(movieReviewList: List<MovieReviewModel>, hasMore: Boolean) {
        val movieReviewListDialog = MovieReviewListDialog.getInstance(movieReviewList, mMovieModel.id, hasMore)
        movieReviewListDialog.show(fragmentManager, "movie_review_dialog")
    }

    override fun showAllTrailers(movieTrailerList: List<MovieTrailerModel>) {
        val movieTrailerListDialog = MovieTrailerListDialog.getInstance(movieTrailerList)
        movieTrailerListDialog.show(fragmentManager, "movie_trailer_dialog")
    }

    private fun showToastMessage(@StringRes messageResId: Int) {
         Snackbar.make(movieDetailContainer, messageResId, Snackbar.LENGTH_SHORT).show()
    }

    override fun setFavoriteButtonEnabled(enabled: Boolean) {
        mFavoriteMenuItem?.isEnabled = enabled
    }

    override fun showLoadingMovieDetailIndicator() {
        rsvMovieDetailRequestStatus.setRequestStatus(RequestStatusDescriptor.LOADING, true)
    }

    override fun hideLoadingMovieDetailIndicator() {
        rsvMovieDetailRequestStatus.setDisplay(false)
    }

    override fun showErrorLoadingMovieDetail(error: Throwable) {
        rsvMovieDetailRequestStatus.setRequestStatus(RequestStatusDescriptor.ERROR)
    }

    override fun showMovieRuntime(hourMinute: Pair<Int, Int>) {
        tvMovieDetailRuntime.text = String.format(getString(R.string.movie_runtime_format), hourMinute.first, hourMinute.second)
    }

    override fun showMessageEmptyReview() {
        // TODO: IMPLEMENT
    }

    override fun hideSeeAllReviewsButton() {
        tvMovieDetailReadAllReviews.setDisplay(false)
    }

    override fun showReadAllReviewsButton(numberOfReviews: Int) {
        tvMovieDetailReadAllReviews.text = String.format(getString(R.string.read_all_reviews_format), numberOfReviews)
    }

    override fun showFirstReviewInfo(movieReviewModel: MovieReviewModel) {
        movieDetailReviewInclude.tvMovieReviewAuthor.text = movieReviewModel.author
        movieDetailReviewInclude.tvMovieReviewContent.text = movieReviewModel.content
    }

    companion object {
        const val MOVIE_KEY = "movie_model"
        fun getDefaultIntent(context: Context, movieModel: MovieModel): Intent {
            return Intent(context, MovieDetailActivity::class.java).also {
                it.putExtra(MOVIE_KEY, movieModel)
            }
        }
    }
}
