package com.themovielist.home.partiallist

import android.os.Bundle
import android.view.View
import com.themovielist.R
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.view.MovieImageViewModel
import kotlinx.android.synthetic.main.recycler_view.*
import timber.log.Timber
import javax.inject.Inject


class HomeMovieListFragment : BaseRecyclerViewFragment<HomeMovieListContract.View>(), HomeMovieListContract.View {
    override val presenterImplementation: BasePresenter<HomeMovieListContract.View>
        get() = mPresenter

    override val viewImplementation: HomeMovieListContract.View
        get() = this

    @Inject
    lateinit var mPresenter: HomeMovieListPresenter

    private val mAdapter by lazy { HomeMovieListAdapter(R.string.the_list_is_empty) }

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
    }

    fun addMovies(movieList: List<MovieModel>) {
        mPresenter.showMovies(movieList)
    }

    override fun showMovies(movieImageViewList: List<MovieImageViewModel>) {
        mAdapter.addItems(movieImageViewList)
    }

    override fun showLoadingIndicator() {
        mAdapter.showLoading()
    }

    override fun showErrorLoadingMovieList(error: Throwable) {
        Timber.e(error, "An error occurred while tried to fetch the movie list")
        mAdapter.showErrorMessage()
    }

    fun hideLoadingIndicator() {
        mAdapter.hideLoadingIndicator()
    }

    fun showEmptyMessageList() {
        mAdapter.showEmptyMessage()
    }

    companion object {
        fun getInstance(): HomeMovieListFragment = HomeMovieListFragment()
    }
}
