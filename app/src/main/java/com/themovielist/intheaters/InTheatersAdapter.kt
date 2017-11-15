package com.themovielist.intheaters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.MovieModel
import com.themovielist.movielist.MovieListViewHolder
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import com.themovielist.util.ApiUtil
import com.themovielist.util.UIUtil

internal class InTheatersAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : CustomRecyclerViewAdapter<MovieModel, InTheatersVH>(emptyMessageResId, tryAgainClickListener) {
    private var mPosterWidth: String? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): InTheatersVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return InTheatersVH(itemView)
    }

    override fun onBindItemViewHolder(holder: InTheatersVH, position: Int) {
        if (mPosterWidth == null) {
            val metrics = UIUtil.getDisplayMetrics(holder.context)
            val posterWidthPx = metrics.widthPixels / 2

            mPosterWidth = ApiUtil.getDefaultPosterSize(posterWidthPx)
        }

        val movieModel = getItemByPosition(position)
        //holder.bind(movieModel, mPosterWidth!!)
    }
}