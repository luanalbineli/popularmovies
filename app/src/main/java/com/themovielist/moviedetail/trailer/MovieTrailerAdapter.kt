package com.themovielist.moviedetail.trailer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.MovieTrailerModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter

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