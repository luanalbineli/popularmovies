package com.themovielist.moviecast

import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import com.themovielist.util.ApiUtil
import com.themovielist.util.dpToPx

internal class MovieCastListAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)? = null) : CustomRecyclerViewAdapter<MovieImageViewModel, MovieCastListVH>(emptyMessageResId, tryAgainClickListener) {
    private var mPosterWidth: String? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieCastListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_movie_list_item, parent, false)
        return MovieCastListVH(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieCastListVH, position: Int) {
        if (mPosterWidth == null) {
            val defaultImageWidth = holder.context.resources.getDimension(R.dimen.home_movie_list_image_width)
            val posterWidthPx = holder.context.dpToPx(defaultImageWidth)

            mPosterWidth = ApiUtil.getDefaultPosterSize(posterWidthPx.toInt())
        }

        val movieImageViewModel = getItemByPosition(position)
        holder.bind(movieImageViewModel, mPosterWidth!!)
    }
}