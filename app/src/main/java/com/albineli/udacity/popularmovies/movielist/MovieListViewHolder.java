package com.albineli.udacity.popularmovies.movielist;

import android.view.View;

import com.albineli.udacity.popularmovies.ui.recyclerview.CustomRecyclerViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

class MovieListViewHolder extends CustomRecyclerViewHolder {
    //@BindView(R.id.sdvMovieItemPoster)
    SimpleDraweeView mMoviePosterSimpleDraweeView;

    MovieListViewHolder(View itemView) {
        super(itemView);

        //ButterKnife.bind(this, itemView);
    }
}
