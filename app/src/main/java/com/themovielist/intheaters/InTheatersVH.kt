package com.themovielist.intheaters

import android.view.View
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*

class InTheatersVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieImageViewModel: MovieImageViewModel, posterWidth: String) {
        movieImageViewModel.movieModel.posterPath?.let {
            // TODO: PUT THE EMPTY IMAGE VIEW ON THE COMPONENT
            val posterUrl = ApiUtil.buildPosterImageUrl(it, posterWidth)
            itemView.mivHomeMovieItem.setImageURI(posterUrl)
        }

        itemView.mivHomeMovieItem.setMovieImageViewModel(movieImageViewModel)
    }
}
