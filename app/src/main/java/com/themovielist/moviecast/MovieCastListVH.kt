package com.themovielist.moviecast

import android.view.View
import com.themovielist.model.MovieCastModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_cast_item.view.*


class MovieCastListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    private val ivMovieCastEmptyImage = itemView.ivMovieCastEmptyImage
    private val sdvMovieCastImage = itemView.sdvMovieCastImage
    private val tvMovieCastCharacter = itemView.tvMovieCastCharacter
    private val tvMovieCastName = itemView.tvMovieCastName

    fun bind(movieCastModel: MovieCastModel, posterWidth: String) {
        if (movieCastModel.profilePath == null) {
            ivMovieCastEmptyImage.setDisplay(true)
            sdvMovieCastImage.setImageURI(null as String?)
        } else {
            val posterUrl = ApiUtil.buildPosterImageUrl(movieCastModel.profilePath, posterWidth)
            ivMovieCastEmptyImage.setDisplay(false)
            sdvMovieCastImage.setImageURI(posterUrl)

        }

        tvMovieCastCharacter.text = movieCastModel.character
        tvMovieCastName.text = movieCastModel.name
    }
}
