package com.themovielist.ui

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.albineli.udacity.popularmovies.R
import com.themovielist.util.setDisplay
import kotlinx.android.synthetic.main.movie_image_view.view.*


class MovieImageView constructor(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.movie_image_view, this)
        sdvMovieImageView.setOnClickListener {
            clMovieImageViewMenuContainer.setDisplay(true)
        }

        clMovieImageViewMenuContainer.setOnClickListener {
            it.setDisplay(false)
        }

        tvMovieImageViewDetailButton.paintFlags = tvMovieImageViewDetailButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvMovieImageViewDetailButton.setOnClickListener {

        }
    }

    fun setImageURI(posterUrl: String) {
        sdvMovieImageView.setImageURI(posterUrl)
    }

    fun setMovieName(movieName: String?) {
        tvMovieImageViewName.text = movieName
    }
}
