package com.albineli.udacity.popularmovies.moviedetail.trailer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.model.MovieTrailerModel
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewAdapter
import timber.log.Timber

class MovieTrailerAdapter : CustomRecyclerViewAdapter<MovieTrailerModel, MovieTrailerViewHolder> {

    constructor(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : super(emptyMessageResId, tryAgainClickListener) {}

    internal constructor() {}

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieTrailerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_trailer_item, parent, false)
        return MovieTrailerViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieTrailerViewHolder, position: Int) {
        val movieTrailerModel = getItemByPosition(position)
       holder.bind(movieTrailerModel)
    }
}