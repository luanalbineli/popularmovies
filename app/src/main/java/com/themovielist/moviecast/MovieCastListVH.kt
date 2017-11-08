package com.themovielist.moviecast

import android.view.View
import com.themovielist.model.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*

class MovieCastListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieImageViewModel: MovieImageViewModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieImageViewModel.movieModel.posterPath, posterWidth)
        itemView.mivHomeMovieItem.setImageURI(posterUrl)
        itemView.mivHomeMovieItem.setMovieImageViewModel(movieImageViewModel)
    }
}
