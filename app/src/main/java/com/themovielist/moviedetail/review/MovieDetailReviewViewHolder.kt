package com.themovielist.moviedetail.review

import android.view.View
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import kotlinx.android.synthetic.main.movie_review_item.view.*

class MovieDetailReviewViewHolder internal constructor(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(author: String, content: String) {
        itemView.tvMovieReviewAuthor.text = author
        itemView.tvMovieReviewContent.text = content
    }
}
