package com.themovielist.movielist

import android.app.Fragment
import android.os.Bundle
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.themovielist.R
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import kotlinx.android.synthetic.main.movie_list_fragment.*
import timber.log.Timber


class MovieListFragment : Fragment() {
    var onTryAgainListener: (() -> Unit)? = null

    var onClickMovieItem: ((position: Int, movieWithGenreModel: MovieImageGenreViewModel) -> Unit)? = null

    var onLoadMoreMovies: (() -> Unit)? = null

    var onChangeListViewType: ((isListViewType: Boolean) -> Unit)? = null

    private lateinit var mAdapter: BaseMovieListAdapter

    private lateinit var mLayoutManager: LinearLayoutManager

    private val mItemViewDecoration by lazy { DividerItemDecoration(activity, mLayoutManager.orientation) }

    private var mIsListViewType = false

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
    }

    private var mListViewTypeMenuItem: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.list_view_type, menu)
        mListViewTypeMenuItem = menu.findItem(R.id.movie_list_view_type)

        toggleListViewTypeMenuItemIcon()
    }

    private fun toggleListViewTypeMenuItemIcon() {
        mListViewTypeMenuItem?.let {
            it.icon = AppCompatResources.getDrawable(activity, if (mIsListViewType) {
                R.drawable.view_grid
            } else {
                R.drawable.view_list
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.movie_list_view_type) {
            mIsListViewType = !mIsListViewType
            if (mIsListViewType) {
                useListLayout()
            } else {
                useGridLayout()
            }
            onChangeListViewType?.invoke(mIsListViewType)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.movie_list_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("rvMovieList: $rvMovieList")
    }

    fun enableLoadMoreListener() {
        // https://codentrick.com/load-more-recyclerview-bottom-progressbar
        disableLoadMoreListener()

        val treshold = if (rvMovieList.layoutManager is GridLayoutManager) {
            (rvMovieList.layoutManager as GridLayoutManager).spanCount
        } else {
            LIST_THRESHOLD
        }

        rvMovieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy == 0) { // Check if the user scrolled down.
                    return
                }
                val totalItemCount = mLayoutManager.itemCount
                val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()
                if (totalItemCount <= lastVisibleItem + treshold) {
                    rvMovieList.post { onLoadMoreMovies?.invoke() }
                }
            }
        })
    }

    fun scrollToItemPosition(firstVisibleItemPosition: Int) {
        mLayoutManager.scrollToPosition(firstVisibleItemPosition)
    }

    fun disableLoadMoreListener() {
        rvMovieList.clearOnScrollListeners()
    }

    fun showLoadingIndicator() {
        mAdapter.showLoading()
    }

    fun showErrorLoadingMovies() {
        mAdapter.showErrorMessage()
    }

    fun hideLoadingIndicator() {
        mAdapter.hideLoadingIndicator()
    }

    fun addMoviesToList(movieWithGenreListModel: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel) {
        mAdapter.configurationImageModel = configurationResponseModel
        mAdapter.addItems(movieWithGenreListModel)
    }

    fun showEmptyListMessage() {
        mAdapter.showEmptyMessage()
    }

    fun getFirstVisibleItemPosition(): Int = mLayoutManager.findFirstVisibleItemPosition()

    fun replaceMoviesToList(finalUpcomingMovieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel) {
        mAdapter.configurationImageModel = imageResponseModel
        mAdapter.replaceItems(finalUpcomingMovieList)
    }

    fun useGridLayout() {
        mIsListViewType = false

        toggleListViewTypeMenuItemIcon()

        mAdapter = MovieGridAdapter(R.string.the_list_is_empty, { onTryAgainListener?.invoke() })
        mLayoutManager = GridLayoutManager(activity, 2).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                        when (mAdapter.getItemViewType(position)) {
                            CustomRecyclerViewAdapter.ViewType.ITEM -> 1
                            else // Grid status.
                            -> it.spanCount
                        }
            }
        }

        configureRecyclerView(false)
    }

    fun useListLayout() {
        mIsListViewType = true

        toggleListViewTypeMenuItemIcon()

        mAdapter = MovieListAdapter(R.string.the_list_is_empty, { onTryAgainListener?.invoke() })
        mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        configureRecyclerView(true)
    }

    private fun configureRecyclerView(useDividerItemDecoration: Boolean) {
        val movieListAdapter = rvMovieList.adapter as? BaseMovieListAdapter
        if (movieListAdapter != null && !movieListAdapter.isStatusError) {
            mAdapter.configurationImageModel = movieListAdapter.configurationImageModel
            mAdapter.addItems(movieListAdapter.items)
        }

        val layoutManager = rvMovieList.layoutManager as? LinearLayoutManager
        if (layoutManager != null) {
            mLayoutManager.scrollToPosition(layoutManager.findFirstVisibleItemPosition())
        }

        mAdapter.setOnItemClickListener { position, item -> onClickMovieItem?.invoke(position, item) }

        if (useDividerItemDecoration) {
            rvMovieList.addItemDecoration(mItemViewDecoration)
        } else {
            rvMovieList.removeItemDecoration(mItemViewDecoration)
        }

        rvMovieList.layoutManager = mLayoutManager
        rvMovieList.adapter = mAdapter
    }

    companion object {
        const val LIST_THRESHOLD = 4
    }
}
