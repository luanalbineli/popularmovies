package com.themovielist.home.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieImageViewModel
import com.themovielist.model.MovieModel
import com.themovielist.ui.recyclerview.HorizonalSpaceItemDecoration
import kotlinx.android.synthetic.main.recycler_view.*
import timber.log.Timber
import javax.inject.Inject


class HomeMovieListFragment : BaseFragment<HomeMovieListContract.View>(), HomeMovieListContract.View {
    override val presenterImplementation: BasePresenter<HomeMovieListContract.View>
        get() = mPresenter

    override val viewImplementation: HomeMovieListContract.View
        get() = this

    @Inject
    lateinit var mPresenter: HomeMovieListPresenter

    private val mMovieListAdapter by lazy { HomeMovieListAdapter(R.string.the_list_is_empty)}

    private val mLayoutManager by lazy { LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dividerAmountOfSpace = activity.resources.getDimension(R.dimen.home_movie_list_image_space)
        val spaceItemViewDecoration = HorizonalSpaceItemDecoration(dividerAmountOfSpace.toInt())

        rvRecyclerView.addItemDecoration(spaceItemViewDecoration)
        rvRecyclerView.layoutManager = mLayoutManager
        rvRecyclerView.adapter = mMovieListAdapter
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
