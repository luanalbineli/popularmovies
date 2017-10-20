package com.themovielist.event

import com.themovielist.enums.MovieListFilterDescriptor

data class TabChangeFilterEvent(@MovieListFilterDescriptor.MovieListFilter val filter: Int)
