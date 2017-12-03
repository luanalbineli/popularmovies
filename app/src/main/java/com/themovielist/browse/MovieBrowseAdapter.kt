package com.themovielist.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.model.MovieCastModel
import com.themovielist.moviecast.MovieCastListVH
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import com.themovielist.util.ApiUtil
import com.themovielist.util.dpToPx

internal class MovieBrowseAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)? = null) : CustomRecyclerViewAdapter<MovieCastModel, MovieCastListVH>(emptyMessageResId, tryAgainClickListener) {
    private var mPosterWidth: String? = null

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieCastListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_cast_item, parent, false)
        return MovieCastListVH(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieCastListVH, position: Int) {
        if (mPosterWidth == null) {
            val defaultImageWidth = holder.context.resources.getDimension(R.dimen.cast_movie_list_image_width)
            val posterWidthPx = holder.context.dpToPx(defaultImageWidth)

            mPosterWidth = ApiUtil.getDefaultPosterSize(posterWidthPx.toInt())
        }

        val movieCastModel = getItemByPosition(position)
        holder.bind(movieCastModel, mPosterWidth!!)
    }
}