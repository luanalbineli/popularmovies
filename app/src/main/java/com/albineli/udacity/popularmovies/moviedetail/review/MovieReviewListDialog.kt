package com.albineli.udacity.popularmovies.moviedetail.review

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.PopularMovieApplication
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.base.BaseFullscreenDialogWithList
import com.albineli.udacity.popularmovies.base.BasePresenter
import com.albineli.udacity.popularmovies.injector.components.ApplicationComponent
import com.albineli.udacity.popularmovies.injector.components.DaggerFragmentComponent
import com.albineli.udacity.popularmovies.model.MovieReviewModel
import java.security.InvalidParameterException
import javax.inject.Inject

class MovieReviewListDialog : BaseFullscreenDialogWithList<MovieReviewModel, MovieReviewListDialogContract.View>(), MovieReviewListDialogContract.View {
    private val mMovieReviewAdapter by lazy { MovieReviewAdapter({ mPresenter.tryLoadReviewsAgain() }) }

    val mLinearLayoutManager by lazy { LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false) }

    private var mHasMore: Boolean = false

    private var mMovieId: Int = 0

    @Inject
    lateinit var mPresenter: MovieReviewListDialogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arguments.containsKey(HAS_MORE_BUNDLE_KEY) || !arguments.containsKey(MOVIE_ID_BUNDLE_KEY)) {
            throw InvalidParameterException("movie")
        }


        mHasMore = arguments.getBoolean(HAS_MORE_BUNDLE_KEY)

        mMovieId = arguments.getInt(MOVIE_ID_BUNDLE_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fullScrennDialogView = super.onCreateView(inflater, container, savedInstanceState)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, mLinearLayoutManager.orientation)

        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = mLinearLayoutManager
        recyclerView.adapter = mMovieReviewAdapter

        return fullScrennDialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.start(mList, mMovieId, mHasMore)

        setTitle(R.string.all_reviews)
    }

    override fun addReviewsToList(movieReviewList: List<MovieReviewModel>) {
        mMovieReviewAdapter.addItems(movieReviewList)
    }

    override fun enableLoadMoreListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy == 0) { // Check if the user scrolled down.
                    return
                }
                val totalItemCount = mLinearLayoutManager.itemCount
                val lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition()
                if (totalItemCount <= lastVisibleItem) {
                    /*java.lang.IllegalStateException: Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data.
                    Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame. */
                    recyclerView.post({ mPresenter.onListEndReached() })
                }
            }
        })
    }

    override fun disableLoadMoreListener() {
        recyclerView.clearOnScrollListeners()
    }

    override fun showLoadingIndicator() {
        mMovieReviewAdapter.showLoading()
    }

    override fun showErrorLoadingReviews() {
        mMovieReviewAdapter.showErrorMessage()
    }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(PopularMovieApplication.getApplicationComponent(activity))
                .build()
                .inject(this)
    }

    override val presenterImplementation: BasePresenter<MovieReviewListDialogContract.View>
        get() = mPresenter

    override val viewImplementation: MovieReviewListDialogContract.View
        get() = this

    companion object {
        private val HAS_MORE_BUNDLE_KEY = "movie_review_has_more"
        private val MOVIE_ID_BUNDLE_KEY = "movie_id"

        fun getInstance(movieModelList: List<MovieReviewModel>, movieId: Int, hasMore: Boolean): MovieReviewListDialog {
            val instance = BaseFullscreenDialogWithList.createNewInstance(MovieReviewListDialog::class.java, movieModelList)
            if (instance.arguments != null) {
                instance.arguments.putBoolean(HAS_MORE_BUNDLE_KEY, hasMore)
                instance.arguments.putInt(MOVIE_ID_BUNDLE_KEY, movieId)
            }

            return instance
        }
    }
}
