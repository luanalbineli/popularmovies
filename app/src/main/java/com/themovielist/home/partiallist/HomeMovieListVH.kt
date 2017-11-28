package com.themovielist.home.partiallist

import android.view.View
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*

class HomeMovieListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    private val mivHomeMovieItem = itemView.mivHomeMovieItem

    fun bind(movieImageViewModel: MovieImageViewModel, posterWidthRequest: String) {
        movieImageViewModel.movieModel.posterPath?.let {
            // TODO: PUT THE EMPTY IMAGE VIEW ON THE COMPONENT
            val posterUrl = ApiUtil.buildPosterImageUrl(it, posterWidthRequest)

            mivHomeMovieItem.setImageURI(posterUrl)
        }
        mivHomeMovieItem.setMovieImageViewModel(movieImageViewModel)


    }
}
