package com.albineli.udacity.popularmovies.moviedetail

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.base.BaseFragment
import com.albineli.udacity.popularmovies.base.BasePresenter
import com.albineli.udacity.popularmovies.event.FavoriteMovieEvent
import com.albineli.udacity.popularmovies.injector.components.ApplicationComponent
import com.albineli.udacity.popularmovies.injector.components.DaggerFragmentComponent
import com.albineli.udacity.popularmovies.model.MovieModel
import com.albineli.udacity.popularmovies.model.MovieReviewModel
import com.albineli.udacity.popularmovies.model.MovieTrailerModel
import com.albineli.udacity.popularmovies.moviedetail.review.MovieReviewAdapter
import com.albineli.udacity.popularmovies.moviedetail.review.MovieReviewListDialog
import com.albineli.udacity.popularmovies.moviedetail.trailer.MovieTrailerAdapter
import com.albineli.udacity.popularmovies.moviedetail.trailer.MovieTrailerListDialog
import com.albineli.udacity.popularmovies.ui.NonScrollableLLM
import com.albineli.udacity.popularmovies.util.ApiUtil
import com.albineli.udacity.popularmovies.util.UIUtil
import com.albineli.udacity.popularmovies.util.YouTubeUtil
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import kotlinx.android.synthetic.main.like_button.*
import org.greenrobot.eventbus.EventBus
import java.security.InvalidParameterException
import javax.inject.Inject


class MovieDetailFragment : BaseFragment<MovieDetailContract.View>(), MovieDetailContract.View {

    override val presenterImplementation: BasePresenter<MovieDetailContract.View>
        get() = mPresenter

    override val viewImplementation: MovieDetailContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieDetailPresenter

    private var mMovieModel: MovieModel? = null

    // @BindView(R.id.tvMovieDetailTitle)
    // internal var mTitleTextView: TextView? = null

    // @BindView(R.id.mrbMovieDetailRatingStar)
    //internal var mRatingBar: MaterialRatingBar? = null

    // @BindView(R.id.tvMovieDetailRating)
    //internal var mRatingTextView: TextView? = null

    // @BindView(R.id.tvMovieDetailSynopsis)
    //internal var mSynopsisTextView: TextView? = null

    // @BindView(R.id.rvMovieDetailReviews)
    //internal var mReviewRecyclerView: RecyclerView? = null

    // @BindView(R.id.tvMovieDetailShowAllReviews)
    //internal var mShowAllReviewsTextView: TextView? = null

    // @BindView(R.id.rvMovieDetailTrailers)
    // internal var mTrailerRecyclerView: RecyclerView? = null

    // @BindView(R.id.tvMovieDetailShowAllTrailers)
    //internal var mShowAllTrailersTextView: TextView? = null

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
        if (arguments == null || !arguments.containsKey(MOVIE_KEY)) {
            throw InvalidParameterException("movie")
        }
        mMovieModel = arguments.getParcelable(MOVIE_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerViews()

        tvMovieDetailShowAllReviews.setOnClickListener { mPresenter.showAllReviews() }
        tvMovieDetailShowAllTrailers.setOnClickListener { mPresenter.showAllTrailers() }

        mPresenter.start(mMovieModel)
    }

    private fun configureRecyclerViews() {
        setUpDefaultRecyclerViewConfiguration(rvMovieDetailReviews)
        rvMovieDetailReviews.adapter = mMovieReviewAdapter

        setUpDefaultRecyclerViewConfiguration(rvMovieDetailTrailers)
        rvMovieDetailTrailers.adapter = mMovieTrailerAdapter
        mMovieTrailerAdapter.setOnItemClickListener { _, item -> YouTubeUtil.openYouTubeVideo(activity, item.key) }
    }

    private fun setUpDefaultRecyclerViewConfiguration(recyclerView: RecyclerView?) {
        val linearLayoutManager = NonScrollableLLM(recyclerView!!.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun showMovieDetail(movieModel: MovieModel) {
        activity.title = movieModel.title

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

        lbMovieDetailFavorite.setOnLikeListener(object : OnLikeListener {
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
        //lbMovieDetailFavorite.isLiked = favorite
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
        movieReviewListDialog.show(childFragmentManager, "movie_review_dialog")
    }

    override fun showAllTrailers(movieTrailerList: List<MovieTrailerModel>) {
        val movieTrailerListDialog = MovieTrailerListDialog.getInstance(movieTrailerList)
        movieTrailerListDialog.show(childFragmentManager, "movie_trailer_dialog")
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

    companion object {
        fun getInstance(movieModel: MovieModel): MovieDetailFragment {
            val bundle = Bundle()
            bundle.putParcelable(MOVIE_KEY, movieModel)

            val movieDetailFragment = MovieDetailFragment()
            movieDetailFragment.arguments = bundle
            return movieDetailFragment
        }

        private val MOVIE_KEY = "movie_model"
    }
}
