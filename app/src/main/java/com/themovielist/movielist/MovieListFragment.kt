package com.themovielist.movielist

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import kotlinx.android.synthetic.main.movie_list_fragment.*


class MovieListFragment : Fragment() {
    var onTryAgainListener: (() -> Unit)? = null

    var onClickMovieItem: ((position: Int, movieWithGenreModel: MovieImageGenreViewModel) -> Unit)? = null

    var onLoadMoreMovies: (() -> Unit)? = null

    private val mMovieListAdapter by lazy { MovieListAdapter(R.string.the_list_is_empty, { onTryAgainListener?.invoke() }) }

    private val mGridLayoutManager by lazy { GridLayoutManager(activity, 2) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.movie_list_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMovieListAdapter.setOnItemClickListener { position, item -> onClickMovieItem?.invoke(position, item) }
        mGridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                    when (mMovieListAdapter.getItemViewType(position)) {
                        CustomRecyclerViewAdapter.ViewType.ITEM -> 1
                        else // Grid status.
                        -> mGridLayoutManager.spanCount
                    }
        }

        rvMovieList.layoutManager = mGridLayoutManager
        rvMovieList.adapter = mMovieListAdapter
    }

    fun enableLoadMoreListener() {
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
                    rvMovieList.post { onLoadMoreMovies?.invoke() }
                }
            }
        })
    }

    fun scrollToItemPosition(firstVisibleItemPosition: Int) {
        mGridLayoutManager.scrollToPosition(firstVisibleItemPosition)
    }

    fun disableLoadMoreListener() {
        rvMovieList.clearOnScrollListeners()
    }

    fun showLoadingIndicator() {
        mMovieListAdapter.showLoading()
    }

    fun showErrorLoadingMovies() {
        mMovieListAdapter.showErrorMessage()
    }

    fun hideLoadingIndicator() {
        mMovieListAdapter.hideLoadingIndicator()
    }

    fun addMoviesToList(movieWithGenreListModel: List<MovieImageGenreViewModel>, configurationResponseModel: ConfigurationImageResponseModel) {
        mMovieListAdapter.configurationImageModel = configurationResponseModel
        mMovieListAdapter.addItems(movieWithGenreListModel)
    }

    fun showEmptyListMessage() {
        mMovieListAdapter.showEmptyMessage()
    }

    fun getFirstVisibleItemPosition(): Int = mGridLayoutManager.findFirstVisibleItemPosition()
    fun replaceMoviesToList(finalUpcomingMovieList: List<MovieImageGenreViewModel>, imageResponseModel: ConfigurationImageResponseModel) {
        mMovieListAdapter.configurationImageModel = imageResponseModel
        mMovieListAdapter.replaceItems(finalUpcomingMovieList)
    }
}
