package com.themovielist.event

import com.themovielist.model.MovieModel


data class FavoriteMovieEvent(val movie: MovieModel, val favorite: Boolean)
