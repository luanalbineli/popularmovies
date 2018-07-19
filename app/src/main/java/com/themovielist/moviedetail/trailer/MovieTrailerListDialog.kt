package com.themovielist.moviedetail.trailer

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.themovielist.PopularMovieApplication
import com.themovielist.R
import com.themovielist.base.BaseFullscreenDialogWithList
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieTrailerModel
import com.themovielist.util.YouTubeUtil
import kotlinx.android.synthetic.main.fullscreen_fragment_dialog_with_list.*
import javax.inject.Inject

class MovieTrailerListDialog : BaseFullscreenDialogWithList<MovieTrailerModel, MovieTrailerListDialogContract.View>(), MovieTrailerListDialogContract.View {
    private val mMovieReviewAdapter by lazy { MovieTrailerAdapter() }

    private val mLinearLayoutManager by lazy { LinearLayoutManager(rvFullscreenFragmentDialog.context, LinearLayoutManager.VERTICAL, false) }

    @Inject
    lateinit var mPresenter: MovieTrailerListDialogPresenter

    override val presenterImplementation: BasePresenter<MovieTrailerListDialogContract.View>
        get() = mPresenter

    override val viewImplementation: MovieTrailerListDialogContract.View
        get() = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMovieReviewAdapter.setOnItemClickListener { _, item -> YouTubeUtil.openYouTubeVideo(activity, item.source) }

        val dividerItemDecoration = DividerItemDecoration(rvFullscreenFragmentDialog.context, mLinearLayoutManager.orientation)

        rvFullscreenFragmentDialog.addItemDecoration(dividerItemDecoration)
        rvFullscreenFragmentDialog.layoutManager = mLinearLayoutManager
        rvFullscreenFragmentDialog.adapter = mMovieReviewAdapter

        mPresenter.start(mList)

        setTitle(R.string.all_trailers)
    }

    override fun showTrailersIntoList(movieReviewList: List<MovieTrailerModel>) {
        mMovieReviewAdapter.addItems(movieReviewList)
    }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(PopularMovieApplication.getApplicationComponent(activity))
                .build()
                .inject(this)
    }

    companion object {

        fun getInstance(movieModelList: List<MovieTrailerModel>): MovieTrailerListDialog {
            return BaseFullscreenDialogWithList.createNewInstance(MovieTrailerListDialog::class.java, movieModelList)
        }
    }
}
