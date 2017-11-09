package com.themovielist.home.list

import android.os.Bundle
import android.view.View
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BasePresenter
import com.themovielist.base.BaseRecyclerViewFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
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

    private val mMovieListAdapter by lazy { HomeMovieListAdapter(R.string.the_list_is_empty) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRecyclerView.adapter = mMovieListAdapter
        useHorizontalSpaceDecorator()
        useLinearLayoutManager()
    }

    fun addMovies(movieList: List<MovieModel>) {
        mPresenter.showMovies(movieList)
    }

    override fun showMovies(movieImageViewList: List<MovieImageViewModel>) {
        mMovieListAdapter.addItems(movieImageViewList)
    }

    override fun showLoadingIndicator() {
        mMovieListAdapter.showLoading()
    }

    override fun showErrorLoadingMovieList(error: Throwable) {
        Timber.e(error, "An error occurred while tried to fetch the movie list")
        mMovieListAdapter.showErrorMessage()
    }

    companion object {
        fun getInstance(): HomeMovieListFragment {
            return HomeMovieListFragment()
        }
    }
}
