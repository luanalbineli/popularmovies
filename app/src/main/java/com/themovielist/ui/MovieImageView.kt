package com.themovielist.ui

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.like.LikeButton
import com.like.OnLikeListener
import com.themovielist.model.MovieImageViewModel
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_image_view.view.*


class MovieImageView constructor(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private lateinit var movieImageViewModel: MovieImageViewModel

    var isFavorite: Boolean = false
        set(value) {
            if (lbMovieImageViewFavorite.isLiked != value) {
                lbMovieImageViewFavorite.isLiked = value
            }
            field = value
        }

    var isMenuOpen: Boolean = false
        set(value) {
            clMovieImageViewMenuContainer.setDisplay(value)
            movieImageViewModel.isMenuOpen = value
            field = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.movie_image_view, this)
        sdvMovieImageView.setOnClickListener {
            isMenuOpen = true
        }

        clMovieImageViewMenuContainer.setOnClickListener {
            isMenuOpen = false
        }

        tvMovieImageViewDetailButton.paintFlags = tvMovieImageViewDetailButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvMovieImageViewDetailButton.setOnClickListener {
            openMovieDetail()
        }

        lbMovieImageViewFavorite.setOnLikeListener(object: OnLikeListener {
            override fun liked(p0: LikeButton?) {
                isFavorite = true
            }

            override fun unLiked(p0: LikeButton?) {
                isFavorite = false
            }
        })
    }

    fun setImageURI(posterUrl: String) {
        sdvMovieImageView.setImageURI(posterUrl)
    }

    fun setMovieImageViewModel(movieImageViewModel: MovieImageViewModel) {
        this.movieImageViewModel = movieImageViewModel

        isFavorite = movieImageViewModel.isFavorite
    }

    fun openMovieDetail() {

    }
}
