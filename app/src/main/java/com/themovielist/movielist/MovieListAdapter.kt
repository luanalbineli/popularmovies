package com.themovielist.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter


internal class MovieListAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : CustomRecyclerViewAdapter<MovieImageGenreViewModel, MovieListVH>(emptyMessageResId, tryAgainClickListener) {
    lateinit var configurationImageModel: ConfigurationImageResponseModel

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieListVH(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieListVH, position: Int) {
        val movieWithGenreModel = getItemByPosition(position)
        holder.bind(movieWithGenreModel, configurationImageModel)
    }
}