package com.themovielist.home.list

import android.view.View
import com.themovielist.model.MovieModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.movie_item.view.*

class HomeMovieListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieModel: MovieModel, mPosterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieModel.posterPath!!, mPosterWidth)
        itemView.sdvMovieItemPoster.setImageURI(posterUrl)
    }
}
