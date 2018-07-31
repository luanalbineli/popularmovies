package com.themovielist.moviedetail.review

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.themovielist.PopularMovieApplication
import com.themovielist.R
import com.themovielist.base.BaseFullscreenDialogWithList
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieReviewModel
import kotlinx.android.synthetic.main.fullscreen_fragment_dialog_with_list.*
import javax.inject.Inject

class MovieReviewListDialog : BaseFullscreenDialogWithList<MovieReviewModel, MovieReviewListDialogContract.View>(), MovieReviewListDialogContract.View {
    private val mMovieReviewAdapter by lazy { MovieReviewAdapter({ mPresenter.tryLoadReviewsAgain() }) }

    val mLinearLayoutManager by lazy { LinearLayoutManager(rvFullscreenFragmentDialog.context, LinearLayoutManager.VERTICAL, false) }

    private var mHasMore: Boolean = false

    private var mMovieId: Int = 0

    @Inject
    lateinit var mPresenter: MovieReviewListDialogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mHasMore = it.getBoolean(HAS_MORE_BUNDLE_KEY)

            mMovieId = it.getInt(MOVIE_ID_BUNDLE_KEY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dividerItemDecoration = DividerItemDecoration(rvFullscreenFragmentDialog.context, mLinearLayoutManager.orientation)

        rvFullscreenFragmentDialog.addItemDecoration(dividerItemDecoration)
        rvFullscreenFragmentDialog.layoutManager = mLinearLayoutManager
        rvFullscreenFragmentDialog.adapter = mMovieReviewAdapter

        mPresenter.start(mList, mMovieId, mHasMore)

        setTitle(R.string.all_reviews)
    }

    override fun addReviewsToList(movieReviewList: List<MovieReviewModel>) {
        mMovieReviewAdapter.addItems(movieReviewList)
    }

    override fun enableLoadMoreListener() {
        rvFullscreenFragmentDialog.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        rvFullscreenFragmentDialog.clearOnScrollListeners()
    }

    override fun showLoadingIndicator() {
        mMovieReviewAdapter.showLoading()
    }

    override fun showErrorLoadingReviews() {
        mMovieReviewAdapter.showErrorMessage()
    }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(PopularMovieApplication.getApplicationComponent(context!!))
                .build()
                .inject(this)
    }

    override val presenterImplementation: BasePresenter<MovieReviewListDialogContract.View>
        get() = mPresenter

    override val viewImplementation: MovieReviewListDialogContract.View
        get() = this

    companion object {
        private const val HAS_MORE_BUNDLE_KEY = "movie_review_has_more"
        private const val MOVIE_ID_BUNDLE_KEY = "movie_id"

        fun getInstance(movieModelList: List<MovieReviewModel>, movieId: Int, hasMore: Boolean): MovieReviewListDialog {
            val instance = BaseFullscreenDialogWithList.createNewInstance(MovieReviewListDialog::class.java, movieModelList)
            instance.arguments?.let {
                it.putBoolean(HAS_MORE_BUNDLE_KEY, hasMore)
                it.putInt(MOVIE_ID_BUNDLE_KEY, movieId)
            }

            return instance
        }
    }
}
