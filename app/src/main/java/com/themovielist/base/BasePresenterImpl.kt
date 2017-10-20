package com.themovielist.base

import com.themovielist.repository.movie.MovieRepository


abstract class BasePresenterImpl(protected val mMovieRepository: MovieRepository)
