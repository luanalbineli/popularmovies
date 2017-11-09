package com.themovielist.moviecast

import android.view.View
import com.themovielist.model.MovieCastModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import kotlinx.android.synthetic.main.movie_cast_item.view.*

class MovieCastListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieCastModel: MovieCastModel, posterWidth: String) {
        val posterUrl = ApiUtil.buildPosterImageUrl(movieCastModel.profilePath, posterWidth)
        itemView.sdvMovieCastImage.setImageURI(posterUrl)
    }
}
