package com.themovielist.moviecast

import android.os.Bundle
import android.view.View
import com.themovielist.R
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieCastModel
import com.themovielist.model.MovieSizeModel
import com.themovielist.model.view.MovieCastViewModel
import kotlinx.android.synthetic.main.recycler_view.*
import timber.log.Timber
import javax.inject.Inject

class MovieCastListFragment : BaseRecyclerViewFragment<MovieCastContract.View>(), MovieCastContract.View {
    override val presenterImplementation: BasePresenter<MovieCastContract.View>
        get() = mPresenter
    override val viewImplementation: MovieCastContract.View
        get() = this

    private val mAdapter by lazy { MovieCastListAdapter(R.string.the_list_is_empty, { mPresenter.tryAgain() }) }

    var movieId: Int? = null
        set(value) {
            mPresenter.setMovieId(value)
        }

    @Inject
    lateinit var mPresenter: MovieCastPresenter

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRecyclerView.adapter = mAdapter

        useHorizontalSpaceDecorator()
        useLinearLayoutManager()

        val movieCastViewModel = buildMovieCastViewModel(savedInstanceState)
        mPresenter.start(movieCastViewModel)
    }

    private fun buildMovieCastViewModel(savedInstanceState: Bundle?): MovieCastViewModel {
        return if (savedInstanceState == null) {
            MovieCastViewModel()
        } else {
            savedInstanceState.getParcelable(MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY)
        }
    }

    override fun showErrorLoadingMovieCast(error: Throwable) {
        Timber.e(error,"An error occurred while tried to fetch the movie cast: ${error.message}")
        mAdapter.showErrorMessage()
    }

    override fun showMovieCastList(movieCastList: List<MovieCastModel>, profileSizeList: List<MovieSizeModel>) {
        mAdapter.addItems(movieCastList)
    }

    override fun hideLoadingIndicator() {
        mAdapter.hideRequestStatus()
    }

    override fun showLoadingIndicator() {
        mAdapter.showLoading()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    companion object {
        const val MOVIE_CAST_VIEW_MODEL_BUNDLE_KEY = "movie_cast_view_model"
    }
}
