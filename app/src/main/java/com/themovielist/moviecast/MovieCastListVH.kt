package com.themovielist.moviecast

import android.view.View
import com.themovielist.model.MovieCastModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_cast_item.view.*


class MovieCastListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(movieCastModel: MovieCastModel, posterWidth: String) {
        if (movieCastModel.profilePath == null) {
            itemView.ivMovieCastEmptyImage.setDisplay(true)
            itemView.sdvMovieCastImage.setImageURI(null as String?)
        } else {
            val posterUrl = ApiUtil.buildPosterImageUrl(movieCastModel.profilePath, posterWidth)
            itemView.sdvMovieCastImage.setImageURI(posterUrl)
            itemView.ivMovieCastEmptyImage.setDisplay(false)

        }

        itemView.tvMovieCastCharacter.text = movieCastModel.character
        itemView.tvMovieCastName.text = movieCastModel.name
    }
}
