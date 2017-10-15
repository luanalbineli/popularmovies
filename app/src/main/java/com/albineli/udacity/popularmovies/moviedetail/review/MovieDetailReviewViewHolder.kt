package com.albineli.udacity.popularmovies.moviedetail.review

import android.view.View
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewHolder
import kotlinx.android.synthetic.main.movie_review_item.view.*

class MovieDetailReviewViewHolder internal constructor(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(author: String, content: String) {
        itemView.tvMovieReviewAuthor.text = author
        itemView.tvMovieReviewContent.text = content
    }
}
