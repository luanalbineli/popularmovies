package com.albineli.udacity.popularmovies.movielist

import android.view.View
import com.albineli.udacity.popularmovies.model.MovieModel
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewHolder
import com.albineli.udacity.popularmovies.util.ApiUtil
import kotlinx.android.synthetic.main.movie_item.view.*

class MovieListViewHolder(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieModel: MovieModel, mPosterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath!!, mPosterWidth)
        itemView.sdvMovieItemPoster.setImageURI(posterUrl)
    }
}
