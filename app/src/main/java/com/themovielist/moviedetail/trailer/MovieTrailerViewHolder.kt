package com.themovielist.moviedetail.trailer

import android.view.View
import com.themovielist.model.MovieTrailerModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import kotlinx.android.synthetic.main.movie_trailer_item.view.*

class MovieTrailerViewHolder internal constructor(itemView: View) : CustomRecyclerViewHolder(itemView) {
    fun bind(movieTrailerModel: MovieTrailerModel) = bindLayout(itemView, movieTrailerModel)

    companion object {
        private val URL_YOUTUBE_THUMBNAIL_FORMAT = "https://img.youtube.com/vi/%1\$s/0.jpg"

        fun bindLayout(itemView: View, movieTrailerModel: MovieTrailerModel) {
            itemView.tvMovieTrailerName.text = movieTrailerModel.name

            itemView.tvMovieTrailerSize.text = movieTrailerModel.size
            itemView.sdvMovieTrailerThumbnail.setImageURI(String.format(URL_YOUTUBE_THUMBNAIL_FORMAT, movieTrailerModel.source))
        }
    }
}
