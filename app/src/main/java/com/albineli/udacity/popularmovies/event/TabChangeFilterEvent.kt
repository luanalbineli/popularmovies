package com.albineli.udacity.popularmovies.event

import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor

data class TabChangeFilterEvent(@MovieListFilterDescriptor.MovieListFilter val filter: Int)
