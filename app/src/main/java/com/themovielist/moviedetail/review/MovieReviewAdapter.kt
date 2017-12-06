package com.themovielist.moviedetail.review

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.model.MovieReviewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter

class MovieReviewAdapter : CustomRecyclerViewAdapter<MovieReviewModel, MovieDetailReviewViewHolder> {
    constructor(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : super(emptyMessageResId, tryAgainClickListener)

    constructor(tryAgainClickListener: (() -> Unit)?) : super(tryAgainClickListener)

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): MovieDetailReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_review_item, parent, false)
        return MovieDetailReviewViewHolder(itemView)
    }

    override fun onBindItemViewHolder(holder: MovieDetailReviewViewHolder, position: Int) {
        val (author, content) = getItemByPosition(position)

        holder.bind(author, content)
    }
}