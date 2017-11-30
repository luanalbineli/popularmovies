package com.themovielist.movielist

import android.view.View
import com.themovielist.model.MovieWithGenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import com.themovielist.util.UIUtil
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.android.synthetic.main.movie_item_movie_image_view.view.*
import timber.log.Timber

class MovieListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    private val mivMovieItem by lazy { itemView.mivMovieItem }

    private val mPosterWidth by lazy {
        val metrics = UIUtil.getDisplayMetrics(context)
        metrics.widthPixels / 2
    }

    private val mPosterHeight by lazy { mPosterWidth * 1.33 }

    fun bind(movieWithGenreModel: MovieWithGenreModel, configurationImageResponseModel: ConfigurationImageResponseModel) {
        val imageUrl = movieWithGenreModel.movieModel.posterPath?.let {
            ApiUtil.buildPosterImageUrl(movieWithGenreModel.movieModel.posterPath!!, configurationImageResponseModel, mPosterWidth, mPosterHeight.toInt())
        }

        mivMovieItem.setImageURI(imageUrl)
    }

    /*itemView.tvMovieItemTitle.text = movieWithGenreModel.movieModel.title
    itemView.tvMovieItemGenre.text = movieWithGenreModel.concatenatedGenres()
    itemView.tvMovieItemReleaseDate.text = movieWithGenreModel.movieModel.releaseDate.toDefaultFormat()*/
}