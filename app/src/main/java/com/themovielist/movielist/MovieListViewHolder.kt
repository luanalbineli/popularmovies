package com.themovielist.movielist

import android.support.v7.widget.RecyclerView
import android.view.View
import com.themovielist.model.MovieModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.movie_item.view.*
import timber.log.Timber

class MovieListViewHolder(itemView: View, val recyclerView: RecyclerView)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieModel: MovieModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath, posterWidth)
        Timber.i("Poster url: $posterUrl")
        itemView.sdvMovieItemPoster.setImageURI(posterUrl)
        //itemView.mivHomeMovieItem.setMovieName(movieModel.title)
    }
}
