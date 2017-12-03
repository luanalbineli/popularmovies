package com.themovielist.moviedetail.trailer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.model.MovieTrailerModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter

class MovieTrailerAdapter internal constructor() : CustomRecyclerViewAdapter<MovieTrailerModel, MovieTrailerViewHolder>() {
    override fun onCreateItemViewHolder(parent: ViewGroup): MovieTrailerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_trailer_item, parent, false)
        return MovieTrailerViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieTrailerViewHolder, position: Int) {
        val movieTrailerModel = getItemByPosition(position)
       holder.bind(movieTrailerModel)
    }
}