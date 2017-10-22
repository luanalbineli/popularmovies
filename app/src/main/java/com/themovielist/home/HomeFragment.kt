package com.themovielist.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.enums.RequestStatusDescriptor
import com.themovielist.home.list.HomeMovieListFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.grid_status.*
import kotlinx.android.synthetic.main.home_fragment.*
import timber.log.Timber
import javax.inject.Inject


class HomeFragment : BaseFragment<HomeContract.View>(), HomeContract.View {
    override val presenterImplementation: BasePresenter<HomeContract.View>
        get() = mPresenter

    override val viewImplementation: HomeContract.View
        get() = this

    @Inject
    lateinit var mPresenter: HomePresenter

    private lateinit var mPopularMovieListFragment: HomeMovieListFragment

    private lateinit var mTopRatedMovieListFragment: HomeMovieListFragment

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setTitle(R.string.home)

        mPopularMovieListFragment = addFragmentIfNotExists(childFragmentManager, R.id.flHomePopularMovieContainer, POPULAR_MOVIE_LIST_TAG) {
            HomeMovieListFragment.getInstance()
        }

        mTopRatedMovieListFragment = addFragmentIfNotExists(childFragmentManager, R.id.flHomeTopRatedMovieContainer, RATING_MOVIE_LIST_TAG) {
            HomeMovieListFragment.getInstance()
        }

        rsvHomeMovieRequestStatus.setTryAgainClickListener { mPresenter.tryToLoadMoviesAgain() }

        mPresenter.start()
    }

    override fun showTopRatedMovies(popularList: List<MovieModel>) {
        mTopRatedMovieListFragment.addMovies(popularList)
    }

    override fun showErrorLoadingMovies(error: Throwable) {
        Timber.i(error, "An error occurred while tried to fecth the movies from HOME")
    }

    override fun showLoadingIndicator() {
        rsvHomeMovieRequestStatus.setRequestStatus(RequestStatusDescriptor.LOADING, true)
    }

    override fun hideLoadingIndicatorAndShowMovies() {
        rsvHomeMovieRequestStatus.setRequestStatus(RequestStatusDescriptor.HIDDEN)
        rsvHomeMovieRequestStatus.setDisplay(false)
        flHomeMovieContainer.setDisplay(true)
    }

    override fun showPopularMovies(popularList: List<MovieModel>) {
        mPopularMovieListFragment.addMovies(popularList)
    }

    companion object {
        private const val POPULAR_MOVIE_LIST_TAG = "popular_movie_list_fragment"
        private const val RATING_MOVIE_LIST_TAG = "rating_movie_list_fragment"
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
