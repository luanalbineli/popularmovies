package com.themovielist.moviedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.albineli.udacity.popularmovies.R
import com.like.LikeButton
import com.like.OnLikeListener
import com.themovielist.base.BaseDaggerActivity
import com.themovielist.base.BasePresenter
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.MovieReviewModel
import com.themovielist.model.MovieTrailerModel
import com.themovielist.moviedetail.review.MovieReviewAdapter
import com.themovielist.moviedetail.review.MovieReviewListDialog
import com.themovielist.moviedetail.trailer.MovieTrailerAdapter
import com.themovielist.moviedetail.trailer.MovieTrailerListDialog
import com.themovielist.ui.NonScrollableLLM
import com.themovielist.util.ApiUtil
import com.themovielist.util.UIUtil
import com.themovielist.util.YouTubeUtil
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.like_button.*
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

    // TODO: CHANGE
    private lateinit var mMovieModel: MovieModel

    private val mMovieReviewAdapter by lazy { MovieReviewAdapter(R.string.there_is_no_reviews_to_show) { mPresenter.tryToLoadReviewAgain() } }
    private val mMovieTrailerAdapter by lazy { MovieTrailerAdapter(R.string.there_is_no_trailers_to_show) { mPresenter.tryToLoadTrailersAgain() } }

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

        setContentView(R.layout.activity_movie_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvMovieDetailShowAllReviews.setOnClickListener { mPresenter.showAllReviews() }
        tvMovieDetailShowAllTrailers.setOnClickListener { mPresenter.showAllTrailers() }

        configureRecyclerViews()

        mPresenter.start(mMovieModel)
    }

    private fun configureRecyclerViews() {
        setUpDefaultRecyclerViewConfiguration(rvMovieDetailReviews)
        rvMovieDetailReviews.adapter = mMovieReviewAdapter

        setUpDefaultRecyclerViewConfiguration(rvMovieDetailTrailers)
        rvMovieDetailTrailers.adapter = mMovieTrailerAdapter
        mMovieTrailerAdapter.setOnItemClickListener { _, item -> YouTubeUtil.openYouTubeVideo(this, item.key) }
    }

    private fun setUpDefaultRecyclerViewConfiguration(recyclerView: RecyclerView?) {
        val linearLayoutManager = NonScrollableLLM(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun showMovieDetail(movieModel: MovieModel) {
        this.title = movieModel.title

        val posterWidth = ApiUtil.getDefaultPosterSize(sdvMovieDetailPoster.width)
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath!!, posterWidth)
        sdvMovieDetailPoster.setImageURI(posterUrl)

        val backdropWidth = ApiUtil.getDefaultPosterSize(UIUtil.getDisplayMetrics(sdvMovieDetailPoster.context).widthPixels)
        val backdropUrl = ApiUtil.buildPosterImageUrl(movieModel.backdropPath!!, backdropWidth)
        ivMovieDetailBackdrop.setImageURI(backdropUrl)


        tvMovieDetailTitle.text = movieModel.title
        tvMovieDetailSynopsis.text = movieModel.overview

        val rating = movieModel.voteAverage.toFloat() / 2f // The range of vote average is 0..10, and of the rating is 0..5
        mrbMovieDetailRatingStar.rating = rating

        tvMovieDetailRating.text = movieModel.voteAverage.toString()

        // TODO: If you use an android:include="myXml", Kotlin does not parse the view type correctly
        Timber.i("lbMovieDetailFavoriteContainer: ${lbMovieDetailFavoriteContainer is LikeButton}")
        (lbMovieDetailFavoriteContainer as LikeButton).setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                mPresenter.saveFavoriteMovie(movieModel)
                EventBus.getDefault().post(FavoriteMovieEvent(movieModel, true))
            }

            override fun unLiked(likeButton: LikeButton) {
                mPresenter.removeFavoriteMovie(movieModel)
                EventBus.getDefault().post(FavoriteMovieEvent(movieModel, false))
            }
        })
    }

    override fun showMovieReview(movieReviewModelList: List<MovieReviewModel>) {
        mMovieReviewAdapter.addItems(movieReviewModelList)
        mMovieReviewAdapter.hideRequestStatus()
    }

    override fun showMovieTrailer(movieTrailerList: List<MovieTrailerModel>) {
        mMovieTrailerAdapter.addItems(movieTrailerList)
        mMovieTrailerAdapter.hideRequestStatus()
    }

    override fun setFavoriteButtonState(favorite: Boolean) {
        (lbMovieDetailFavoriteContainer as LikeButton).isLiked = favorite
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

    override fun showErrorMessageLoadReviews() {
        mMovieReviewAdapter.showErrorMessage()
    }

    override fun showErrorMessageLoadTrailers() {
        mMovieTrailerAdapter.showErrorMessage()
    }

    override fun setShowAllReviewsButtonVisibility(visible: Boolean) {
        tvMovieDetailShowAllReviews.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setShowAllTrailersButtonVisibility(visible: Boolean) {
        tvMovieDetailShowAllTrailers.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showLoadingReviewsIndicator() {
        mMovieReviewAdapter.showLoading()
    }

    override fun showLoadingTrailersIndicator() {
        mMovieTrailerAdapter.showLoading()
    }

    override fun showAllReviews(movieReviewList: List<MovieReviewModel>, hasMore: Boolean) {
        val movieReviewListDialog = MovieReviewListDialog.getInstance(movieReviewList, mMovieModel!!.id, hasMore)
        movieReviewListDialog.show(fragmentManager, "movie_review_dialog")
    }

    override fun showAllTrailers(movieTrailerList: List<MovieTrailerModel>) {
        val movieTrailerListDialog = MovieTrailerListDialog.getInstance(movieTrailerList)
        movieTrailerListDialog.show(fragmentManager, "movie_trailer_dialog")
    }

    override fun showEmptyReviewListMessage() {
        mMovieReviewAdapter.showEmptyMessage()
    }

    override fun showEmptyTrailerListMessage() {
        mMovieTrailerAdapter.showEmptyMessage()
    }

    private fun showToastMessage(@StringRes messageResId: Int) {
        Snackbar.make(clMovieDetailContainer, messageResId, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
