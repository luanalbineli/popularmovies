package com.themovielist.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R


internal class MovieGridAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : BaseMovieListAdapter(emptyMessageResId, tryAgainClickListener) {
    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): MovieListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_grid_item, parent, false)
        return MovieListVH(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieListVH, position: Int) {
        val movieWithGenreModel = getItemByPosition(position)
        holder.bind(movieWithGenreModel, configurationImageModel)
    }
}