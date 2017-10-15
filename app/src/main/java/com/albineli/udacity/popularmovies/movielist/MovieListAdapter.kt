package com.albineli.udacity.popularmovies.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.model.MovieModel
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewAdapter
import com.albineli.udacity.popularmovies.util.ApiUtil
import com.albineli.udacity.popularmovies.util.UIUtil

internal class MovieListAdapter (emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : CustomRecyclerViewAdapter<MovieModel, MovieListViewHolder>(emptyMessageResId, tryAgainClickListener) {
    private var mPosterWidth: String? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieListViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieListViewHolder, position: Int) {
        if (mPosterWidth == null) {
            val metrics = UIUtil.getDisplayMetrics(holder.context)
            val posterWidthPx = metrics.widthPixels / MovieListFragment.getItensPerRow(holder.context)

            mPosterWidth = ApiUtil.getDefaultPosterSize(posterWidthPx)
        }

        val movieModel = getItemByPosition(position)
        holder.bind(movieModel, mPosterWidth!!)
    }
}