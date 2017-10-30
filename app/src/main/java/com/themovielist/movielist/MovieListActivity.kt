package com.themovielist.movielist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import com.albineli.udacity.popularmovies.R
import com.themovielist.base.BaseDaggerActivity
import com.themovielist.base.BasePresenter
import com.themovielist.enums.MovieSortEnum
import com.themovielist.event.FavoriteMovieEvent
import com.themovielist.event.TabChangeFilterEvent
import com.themovielist.injector.components.ApplicationComponent
import com.themovielist.injector.components.DaggerFragmentComponent
import com.themovielist.model.MovieListStateModel
import com.themovielist.model.MovieModel
import com.themovielist.moviedetail.MovieDetailActivity
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import kotlinx.android.synthetic.main.movie_list_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject


class MovieListActivity : BaseDaggerActivity<MovieListContract.View>(), MovieListContract.View {

    override val presenterImplementation: BasePresenter<MovieListContract.View>
        get() = mPresenter

    override val viewImplementation: MovieListContract.View
        get() = this

    @Inject
    lateinit var mPresenter: MovieListPresenter

    private val mMovieListAdapter by lazy {MovieListAdapter(R.string.the_list_is_empty, { mPresenter.tryAgain() })}

    private val mGridLayoutManager by lazy { GridLayoutManager(this, 2) }

    override fun onInjectDependencies(applicationComponent: ApplicationComponent) {
        DaggerFragmentComponent.builder()
                .applicationComponent(applicationComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.movie_list_activity)

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
        rvMovieList.layoutManager = mGridLayoutManager
        rvMovieList.adapter = mMovieListAdapter

        val movieListStateModel = if (savedInstanceState != null)
            MovieListStateModel.getFromBundle(savedInstanceState)
        else
            MovieListStateModel.getFromIntent(intent)

        mPresenter.init(movieListStateModel)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    private var mMenu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu

        menuInflater.inflate(R.menu.movie_list, menu)

        return true
    }

    override fun setTitleByFilter(@MovieSortEnum.MovieSort filter: Int) {
        var titleStringResId = R.string.popular
        if (filter == MovieSortEnum.RATING) {
            titleStringResId = R.string.rating
        } else if (filter == MovieSortEnum.FAVORITE) {
            titleStringResId = R.string.favorite
        }

        val title = getString(titleStringResId)
        Timber.d("Setting the title: " + title)
        supportActionBar?.title = title
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
        val movieDetailIntent = MovieDetailActivity.getDefaultIntent(this, movieModel)
        startActivity(movieDetailIntent)
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
        rvMovieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    rvMovieList.post { mPresenter.onListEndReached() }
                }
            }
        })
    }

    override fun disableLoadMoreListener() {
        rvMovieList.clearOnScrollListeners()
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
        const val FILTER_BUNDLE_KEY = "movie_list_filter_bundle"

        fun getIntent(context: Context, @MovieSortEnum.MovieSort filter: Int): Intent {
            val intent = Intent(context, MovieListActivity::class.java)
            intent.putExtra(FILTER_BUNDLE_KEY, filter)
            return intent
        }
    }
}
