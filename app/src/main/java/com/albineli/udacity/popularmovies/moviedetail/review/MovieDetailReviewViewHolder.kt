package com.albineli.udacity.popularmovies.moviedetail.review

import android.view.View
import android.widget.TextView
import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewHolder
import com.borjabravo.readmoretextview.ReadMoreTextView

class MovieDetailReviewViewHolder internal constructor(itemView: View)//  ButterKnife.bind(this, itemView);
    : CustomRecyclerViewHolder(itemView) {
    // @BindView(R.id.tvMovieReviewAuthor)
    internal var mAuthorTextView: TextView? = null

    //@BindView(R.id.tvMovieReviewContent)
    internal var mContentTextView: ReadMoreTextView? = null
}
