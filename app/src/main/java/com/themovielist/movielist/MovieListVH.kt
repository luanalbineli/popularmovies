package com.themovielist.movielist

import android.view.View
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import com.themovielist.util.ApiUtil
import com.themovielist.util.UIUtil
import com.themovielist.util.yearFromCalendar
import kotlinx.android.synthetic.main.movie_grid_item.view.*
import kotlinx.android.synthetic.main.movie_item_movie_image_view.view.*

class MovieListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    private val mivMovieItem = itemView.mivMovieItem
    private val tvMovieItemGenre = itemView.tvMovieItemGenre
    private val tvMovieDetailRating = itemView.tvMovieDetailRating
    private val tvMovieDetailYear = itemView.tvMovieDetailYear

    private val mPosterWidth by lazy {
        val metrics = UIUtil.getDisplayMetrics(context)
        metrics.widthPixels / 2
    }

    private val mPosterHeight by lazy { mPosterWidth * 1.33 }

    fun bind(movieImageGenreViewModel: MovieImageGenreViewModel, configurationImageResponseModel: ConfigurationImageResponseModel) {
        val imageUrl = movieImageGenreViewModel.movieModel.posterPath?.let {
            ApiUtil.buildPosterImageUrl(movieImageGenreViewModel.movieModel.posterPath!!, configurationImageResponseModel, mPosterWidth, mPosterHeight.toInt())
        }

        mivMovieItem.setImageURI(imageUrl)
        mivMovieItem.setMovieImageViewModel(movieImageGenreViewModel)
        tvMovieItemGenre.text = movieImageGenreViewModel.genreList?.let {
            if (it.isEmpty()) {
                ""
            } else {
                it.map { it.name }.reduce { a, b -> "$a, $b" }
            }
        } ?: ""

        tvMovieDetailRating.text = movieImageGenreViewModel.movieModel.voteAverage.toString()
        tvMovieDetailYear.text = movieImageGenreViewModel.movieModel.releaseDate?.yearFromCalendar?.toString() ?: ""
    }


}