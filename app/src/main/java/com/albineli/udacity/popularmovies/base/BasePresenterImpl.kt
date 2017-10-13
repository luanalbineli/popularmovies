package com.albineli.udacity.popularmovies.base

import com.albineli.udacity.popularmovies.repository.movie.MovieRepository


abstract class BasePresenterImpl(protected val mMovieRepository: MovieRepository)
