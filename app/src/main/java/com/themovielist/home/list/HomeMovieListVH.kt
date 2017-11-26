package com.themovielist.home.list

import android.view.View
import com.albineli.udacity.popularmovies.R
import com.themovielist.model.view.MovieImageViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.home_movie_list_item.view.*

class HomeMovieListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    private val mivHomeMovieItem = itemView.mivHomeMovieItem

    fun bind(movieImageViewModel: MovieImageViewModel, posterWidthRequest: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieImageViewModel.movieModel.posterPath, posterWidthRequest)

        val posterWidth = context.resources.getDimensionPixelSize(R.dimen.home_movie_list_image_width)
        val posterHeight = context.resources.getDimensionPixelSize(R.dimen.home_movie_list_image_height)

        mivHomeMovieItem.setImageURI(posterUrl, posterWidth, posterHeight)
        mivHomeMovieItem.setMovieImageViewModel(movieImageViewModel)
    }
}
