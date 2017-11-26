package com.themovielist.movielist

import android.view.View
import com.themovielist.model.MovieModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.movie_item.view.*
import timber.log.Timber

class MovieListViewHolder(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    //private val mivMovieItem = itemView.mivMovieItem
    private val sdvMovieItemPoster = itemView.sdvMovieItemPoster

    fun bind(movieModel: MovieModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath, posterWidth)
        //mivMovieItem.setImageURI(posterUrl, posterHeight = 180)
        //mivMovieItem.
        Timber.i("Poster url: $posterUrl")
        itemView.sdvMovieItemPoster.setImageURI(posterUrl)
        //itemView.mivHomeMovieItem.setMovieName(movieModel.title)
    }
}
