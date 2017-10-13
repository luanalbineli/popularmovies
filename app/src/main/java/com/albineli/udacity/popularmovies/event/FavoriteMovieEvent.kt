package com.albineli.udacity.popularmovies.event

import com.albineli.udacity.popularmovies.model.MovieModel


data class FavoriteMovieEvent(val movie: MovieModel, val favorite: Boolean)
