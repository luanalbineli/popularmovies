package com.themovielist.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.enums.RequestStatusDescriptor
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.home.fulllist.HomeFullMovieListActivity
import com.themovielist.home.partiallist.HomeMovieListFragment
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieModel
import com.themovielist.model.view.HomeViewModel
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.home_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
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

        tvHomeSeeMovieCast.setOnClickListener { mPresenter.seeAllPopularMovieList() }
        tvHomeSeeMovieListByRating.setOnClickListener { mPresenter.sellAllRatingMovieList() }

        val homeViewModel = savedInstanceState?.getParcelable(HOME_VIEW_MODEL_BUNDLE_KEY) ?: HomeViewModel()
        mPresenter.start(homeViewModel)
    }

    override fun showTopRatedMovies(topRatedList: List<MovieModel>) {
        mTopRatedMovieListFragment.addMovies(topRatedList)
    }

    override fun showErrorLoadingMovies(error: Throwable) {
        Timber.i(error, "An error occurred while tried to fecth the movies from HOME")
        rsvHomeMovieRequestStatus.setRequestStatus(RequestStatusDescriptor.ERROR, true)
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

    override fun seeAllMoviesSortedBy(homeMovieSort: Int) {
        val intent = HomeFullMovieListActivity.getIntent(activity, homeMovieSort)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelable(HOME_VIEW_MODEL_BUNDLE_KEY, mPresenter.viewModel)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoriteMovieEvent(favoriteMovieEvent: FavoriteMovieEvent) {
        mPresenter.onFavoriteMovieEvent(favoriteMovieEvent.movie, favoriteMovieEvent.favorite)
    }

    companion object {
        private const val POPULAR_MOVIE_LIST_TAG = "popular_movie_list_fragment"
        private const val RATING_MOVIE_LIST_TAG = "rating_movie_list_fragment"
        private const val HOME_VIEW_MODEL_BUNDLE_KEY = "home_view_model_bundle_key"
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
