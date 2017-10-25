package com.themovielist.movielist

import android.view.View
import com.themovielist.model.MovieModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*
import timber.log.Timber

class MovieListViewHolder(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieModel: MovieModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath, posterWidth)
        Timber.i("Poster url: $posterUrl")
        itemView.mivHomeMovieItem.setImageURI(posterUrl)
        //itemView.mivHomeMovieItem.setMovieName(movieModel.title)
    }
}
