package com.albineli.udacity.popularmovies.moviedetail.trailer

import android.view.View
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.model.MovieTrailerModel
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewHolder
import kotlinx.android.synthetic.main.movie_trailer_item.view.*
import timber.log.Timber

class MovieTrailerViewHolder internal constructor(itemView: View) : CustomRecyclerViewHolder(itemView) {
    fun bind(movieTrailerModel: MovieTrailerModel) {
        itemView.tvMovieTrailerName.text = movieTrailerModel.name

        Timber.i("Bindind trailer: " + movieTrailerModel)

        val sizeText = String.format(context.getString(R.string.screen_size_trailer), movieTrailerModel.size)
        itemView.tvMovieTrailerSize.text = sizeText

        if (movieTrailerModel.site.equals("YouTube", ignoreCase = true)) { // TODO: I didn't find a non youtube video, but I must handle it
            itemView.sdvMovieTrailerThumbnail.setImageURI(String.format(URL_YOUTUBE_THUMBNAIL_FORMAT, movieTrailerModel.key))
            itemView.tvMovieTrailerSource.setText(R.string.youtube)
            itemView.tvMovieTrailerSource.visibility = View.VISIBLE
        } else {
            itemView.tvMovieTrailerSource.visibility = View.GONE
        }
    }

    companion object {
        private val URL_YOUTUBE_THUMBNAIL_FORMAT = "https://img.youtube.com/vi/%1\$s/0.jpg"
    }
}
