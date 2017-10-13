package com.albineli.udacity.popularmovies.moviedetail.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.albineli.udacity.popularmovies.R
import com.albineli.udacity.popularmovies.model.MovieReviewModel
import com.albineli.udacity.popularmovies.ui.RequestStatusView
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewAdapter

class MovieReviewAdapter : CustomRecyclerViewAdapter<MovieReviewModel, MovieDetailReviewViewHolder> {
    constructor(emptyMessageResId: Int, tryAgainClickListener: RequestStatusView.ITryAgainClickListener) : super(emptyMessageResId, tryAgainClickListener) {}

    constructor(tryAgainClickListener: RequestStatusView.ITryAgainClickListener) : super(tryAgainClickListener) {}

    override fun onCreateItemViewHolder(parent: ViewGroup): MovieDetailReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_review_item, parent, false)
        return MovieDetailReviewViewHolder(itemView)
    }

    override fun onBindItemViewHolder(movieListViewHolder: MovieDetailReviewViewHolder, position: Int) {
        val (author, content) = getItemByPosition(position)

        // TODO: Butterknife
        /*movieListViewHolder.mAuthorTextView.setText(author)
        movieListViewHolder.mContentTextView.setText(content)*/
    }
}