package com.themovielist.intheaters

import android.view.View
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*

class InTheatersVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieImageViewModel: MovieImageViewModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieImageViewModel.movieModel.posterPath, posterWidth)
        itemView.mivHomeMovieItem.setImageURI(posterUrl)
        itemView.mivHomeMovieItem.setMovieImageViewModel(movieImageViewModel)
    }
}
