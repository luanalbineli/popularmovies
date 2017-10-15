package com.albineli.udacity.popularmovies.moviedetail.trailer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.model.MovieTrailerModel
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewAdapter
import timber.log.Timber

class MovieTrailerAdapter : CustomRecyclerViewAdapter<MovieTrailerModel, MovieTrailerViewHolder> {

    constructor(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : super(emptyMessageResId, tryAgainClickListener) {}

    internal constructor() {}

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieTrailerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_trailer_item, parent, false)
        return MovieTrailerViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieTrailerViewHolder, position: Int) {
        val movieTrailerModel = getItemByPosition(position)
        holder.mNameTextView.text = movieTrailerModel.name

        Timber.i("Bindind trailer: " + movieTrailerModel)

        val sizeText = String.format(holder.context.getString(R.string.screen_size_trailer), movieTrailerModel.size)
        holder.mSizeTextView.text = sizeText

        if (movieTrailerModel.site.equals("YouTube", ignoreCase = true)) { // TODO: I didn't find a non youtube video, but I must handle it
            holder.mThumbnailSimpleDraweeView.setImageURI(String.format(URL_YOUTUBE_THUMBNAIL_FORMAT, movieTrailerModel.key))
            holder.mSourceTextView.setText(R.string.youtube)
            holder.mSourceTextView.visibility = View.VISIBLE
        } else {
            holder.mSourceTextView.visibility = View.GONE
        }
    }

    companion object {
        private val URL_YOUTUBE_THUMBNAIL_FORMAT = "https://img.youtube.com/vi/%1\$s/0.jpg"
    }
}