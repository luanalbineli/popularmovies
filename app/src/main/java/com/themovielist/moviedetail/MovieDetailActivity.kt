package com.themovielist.moviedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import com.themovielist.R
import com.themovielist.base.BaseDaggerActivity
import com.themovielist.base.BasePresenter
import com.themovielist.enums.RequestStatusDescriptor
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.moviecast.MovieCastListFragment
import com.themovielist.moviedetail.review.MovieDetailReviewViewHolder
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerViewHolder
import com.themovielist.recommendation.MovieRecommendationFragment
import com.themovielist.ui.MovieDetailSectionView
import com.themovielist.util.*
import kotlinx.android.synthetic.main.movie_detail_activity.*
import kotlinx.android.synthetic.main.movie_header_detail.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.security.InvalidParameterException
import javax.inject.Inject


class MovieDetailActivity : BaseDaggerActivity<MovieDetailContract.View>(), MovieDetailContract.View {
    override val presenterImplementation: BasePresenter<MovieDetailContract.View>
        get() = mPresenter

    override val viewImplementation: MovieDetailContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieDetailPresenter

    private var mFavoriteMenuItem: MenuItem? = null

    private val mMovieCastFragment by lazy { fragmentManager.findFragmentById(R.id.fragmentMovieDetailCast) as MovieCastListFragment }

    private val mMovieRecommendationListFragment by lazy { fragmentManager.findFragmentById(R.id.fragmentMovieRecommendationList) as MovieRecommendationFragment }

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

        val movieModel = intent.getParcelableExtra<MovieModel>(MOVIE_KEY)

        setContentView(R.layout.movie_detail_activity)

        // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configureToolbar()

        rsvMovieDetailRequestStatus.setTryAgainClickListener { mPresenter.tryFecthMovieDetailAgain() }

        mMovieCastFragment.movieId = movieModel.id

        mMovieRecommendationListFragment.movieId = movieModel.id

        mPresenter.start(movieModel)
    }

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

        var isShow = false
        var scrollRange = -1
       /* appBarLayoutMovieDetail.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener({ appBarLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset == 0) {
                collapse_toolbar.title = mPresenter.getMovieName()
                isShow = true
            } else if(isShow) {
                collapse_toolbar.title = " "
                isShow = false
            }
        }))*/
    }

    override fun showMovieInfo(movieWithGenreModel: MovieWithGenreModel) {
        val backdropWidth = ApiUtil.getDefaultPosterSize(UIUtil.getDisplayMetrics(sdvMovieHeaderBackdrop.context).widthPixels)
        movieWithGenreModel.movieModel.backdropPath?.let {
            val backdropUrl = ApiUtil.buildPosterImageUrl(it, backdropWidth)
            sdvMovieHeaderBackdrop.setImageURI(backdropUrl)
        }

        tvMovieHeaderMovieName.text = movieWithGenreModel.movieModel.title
        tvMovieHeaderMovieGenres.text = movieWithGenreModel.concatenatedGenres()
        Timber.i("RELEASE DATE: ${movieWithGenreModel.movieModel.releaseDate} | YEAR: ${movieWithGenreModel.movieModel.releaseDate?.yearFromCalendar}")
        tvMovieHeaderReleaseDate.text = movieWithGenreModel.movieModel.releaseDate?.yearFromCalendar.toString()

        tvMovieDetailSynopsys.text = movieWithGenreModel.movieModel.overview

        tvMovieDetailRating.text = movieWithGenreModel.movieModel.voteAverage.toString()
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

    override fun showAllReviews(movieReviewList: List<MovieReviewModel>, hasMore: Boolean, movieId: Int) {
        val movieReviewListDialog = MovieReviewListDialog.getInstance(movieReviewList, movieId, hasMore)
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
        Timber.e(error, "An error occurred while tried to fetch the movie detail: ${error.message}")
        rsvMovieDetailRequestStatus.setRequestStatus(RequestStatusDescriptor.ERROR, true)
    }

    override fun showMovieRuntime(hourMinute: Pair<Int, Int>) {
        tvMovieDetailRuntime.text = String.format(getString(R.string.movie_runtime_format), hourMinute.first, hourMinute.second)
    }

    override fun bindMovieReviewInfo(movieReviewList: List<MovieReviewModel>) {

        val reviewSection = findViewById(R.id.mdsMovieDetailReviewSection) as MovieDetailSectionView<MovieReviewModel>
        reviewSection.onBindSectionContent = { itemView, movieReviewModel ->
            MovieDetailReviewViewHolder.bindLayout(itemView, movieReviewModel.author, movieReviewModel.content)
        }
        reviewSection.onClickSectionButton = {
            mPresenter.showAllReviews()
        }

        reviewSection.bind(movieReviewList)
    }

    override fun bindMovieTrailerInfo(movieTrailerList: List<MovieTrailerModel>) {
        val trailerSection = findViewById(R.id.mdsMovieDetailTrailerSection) as MovieDetailSectionView<MovieTrailerModel>
        trailerSection.onBindSectionContent = { itemView, movieTrailerModel ->
            MovieTrailerViewHolder.bindLayout(itemView, movieTrailerModel)
            itemView.setOnClickListener { YouTubeUtil.openYouTubeVideo(this, movieTrailerModel.source) }
        }
        trailerSection.onClickSectionButton = {
            mPresenter.showAllTrailers()
        }

        trailerSection.bind(movieTrailerList)
    }

    override fun dispatchFavoriteMovieEvent(movieModel: MovieModel, isFavorite: Boolean) {
        EventBus.getDefault().post(FavoriteMovieEvent(movieModel, isFavorite))
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
