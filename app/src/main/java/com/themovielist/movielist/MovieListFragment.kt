package com.themovielist.movielist

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseFragment
import com.themovielist.base.BasePresenter
import com.themovielist.enums.MovieListFilterDescriptor
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.event.TabChangeFilterEvent
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel
import com.themovielist.moviedetail.MovieDetailFragment
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_movie_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.security.InvalidParameterException
import javax.inject.Inject


class MovieListFragment : BaseFragment<MovieListContract.View>(), MovieListContract.View {

    override val presenterImplementation: BasePresenter<MovieListContract.View>
        get() = mPresenter

    override val viewImplementation: MovieListContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieListPresenter

    private val mMovieListAdapter by lazy {MovieListAdapter(R.string.the_list_is_empty, { mPresenter.tryAgain() })}

    private val mGridLayoutManager by lazy { GridLayoutManager(activity, getItensPerRow(activity)) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (arguments == null || !arguments.containsKey(FILTER_BUNDLE_KEY)) {
            throw InvalidParameterException("filter")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List.
        mMovieListAdapter.setOnItemClickListener({ position, movieModel -> mPresenter.openMovieDetail(position, movieModel) })

        mGridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mMovieListAdapter.getItemViewType(position)) {
                    CustomRecyclerViewAdapter.ViewType.ITEM -> 1
                    else // Grid status.
                    -> mGridLayoutManager.spanCount
                }
            }
        }
        rv_movie_list.layoutManager = mGridLayoutManager
        rv_movie_list.adapter = mMovieListAdapter

        var movieListStateModel = MovieListStateModel.getFromBundle(savedInstanceState)
        if (movieListStateModel == null) {
            movieListStateModel = MovieListStateModel.getFromArguments(arguments)
        }

        mPresenter.init(movieListStateModel)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        fragmentManager
                .addOnBackStackChangedListener {
                    Timber.i("Changed the backstack: " + fragmentManager.backStackEntryCount)
                    val visible = fragmentManager.backStackEntryCount == 0
                    mPresenter.onVisibilityChanged(visible)
                }
    }

    override fun setTitleByFilter(@MovieListFilterDescriptor.MovieListFilter filter: Int) {
        var titleStringResId = R.string.popular
        if (filter == MovieListFilterDescriptor.RATING) {
            titleStringResId = R.string.rating
        } else if (filter == MovieListFilterDescriptor.FAVORITE) {
            titleStringResId = R.string.favorite
        }

        val title = getString(titleStringResId)
        Timber.d("Setting the title: " + title)
        activity.title = title
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)
        mPresenter.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFavoriteMovieEvent(favoriteMovieEvent: FavoriteMovieEvent) {
        mPresenter.favoriteMovie(favoriteMovieEvent.movie, favoriteMovieEvent.favorite)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabChangeFilterEvent(tabChangeFilterEvent: TabChangeFilterEvent) {
        mPresenter.changeFilterList(tabChangeFilterEvent.filter)

        if (fragmentManager.backStackEntryCount > 0) { // If is at detail screen
            fragmentManager.popBackStack()
        }
    }

    override fun showLoadingMovieListError() {
        mMovieListAdapter.showErrorMessage()
    }

    override fun showMovieList(movieList: List<MovieModel>, replaceData: Boolean) {
        if (replaceData) {
            mMovieListAdapter.replaceItems(movieList)
        } else {
            mMovieListAdapter.addItems(movieList)
        }
    }

    override fun showMovieDetail(movieModel: MovieModel) {
        val movieDetailFragment = MovieDetailFragment.getInstance(movieModel)
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.flMainContent, movieDetailFragment)
                .addToBackStack(null)
                .commit()
    }

    override fun clearMovieList() {
        mMovieListAdapter.clearItems()
    }

    override fun showEmptyListMessage() {
        mMovieListAdapter.showEmptyMessage()
    }

    override fun hideRequestStatus() {
        mMovieListAdapter.hideRequestStatus()
    }

    override fun showLoadingIndicator() {
        mMovieListAdapter.showLoading()
    }

    override fun enableLoadMoreListener() {
        // https://codentrick.com/load-more-recyclerview-bottom-progressbar
        disableLoadMoreListener()
        rv_movie_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy == 0) { // Check if the user scrolled down.
                    return
                }
                val totalItemCount = mGridLayoutManager.itemCount
                val lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition()
                if (totalItemCount <= lastVisibleItem + mGridLayoutManager.spanCount) {
                    /*java.lang.IllegalStateException: Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data.
                    Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame. */
                    rv_movie_list.post { mPresenter.onListEndReached() }
                }
            }
        })
    }

    override fun disableLoadMoreListener() {
        rv_movie_list.clearOnScrollListeners()
    }

    override fun removeMovieFromListByIndex(index: Int) {
        mMovieListAdapter.removeItemByIndex(index)
    }

    override fun addMovieToListByIndex(index: Int, movieModel: MovieModel) {
        mMovieListAdapter.insertItemByIndex(movieModel, index)
    }

    override val movieListCount: Int
        get() = mMovieListAdapter.itemCount

    override fun scrollToMovieIndex(firstVisibleMovieIndex: Int) {
        mGridLayoutManager.scrollToPosition(firstVisibleMovieIndex)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("Saving the state - pageIndex: " + mPresenter.pageIndex +
                "\nselectedMovieIndex: " + mPresenter.selectedMovieIndex +
                "\nfirst visible item index: " + mGridLayoutManager.findFirstVisibleItemPosition())

        MovieListStateModel.saveToBundle(outState, mMovieListAdapter.items, mPresenter.filter, mPresenter.pageIndex, mPresenter.selectedMovieIndex, mGridLayoutManager.findFirstVisibleItemPosition())
    }

    companion object {
        private val FILTER_BUNDLE_KEY = "movie_list_filter_bundle"

        fun getInstance(@MovieListFilterDescriptor.MovieListFilter filter: Int): MovieListFragment {
            val bundle = Bundle()
            bundle.putInt(FILTER_BUNDLE_KEY, filter)
            val movieListFragment = MovieListFragment()
            movieListFragment.arguments = bundle

            return movieListFragment
        }

        fun getItensPerRow(context: Context): Int {
            val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val isTablet = context.resources.getBoolean(R.bool.isTablet)
            if (isTablet) {
                return if (isPortrait) 5 else 6
            }
            return if (isPortrait) 3 else 4
        }
    }
}
