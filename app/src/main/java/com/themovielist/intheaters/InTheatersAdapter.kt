package com.themovielist.intheaters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.MovieModel
import com.themovielist.movielist.MovieListViewHolder
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import com.themovielist.util.ApiUtil
import com.themovielist.util.UIUtil

internal class InTheatersAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : CustomRecyclerViewAdapter<MovieModel, MovieListViewHolder>(emptyMessageResId, tryAgainClickListener) {
    private var mPosterWidth: String? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieListViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieListViewHolder, position: Int) {
        if (mPosterWidth == null) {
            val metrics = UIUtil.getDisplayMetrics(holder.context)
            val posterWidthPx = metrics.widthPixels / 2

            mPosterWidth = ApiUtil.getDefaultPosterSize(posterWidthPx)
        }

        val movieModel = getItemByPosition(position)
        holder.bind(movieModel, mPosterWidth!!)
    }
}