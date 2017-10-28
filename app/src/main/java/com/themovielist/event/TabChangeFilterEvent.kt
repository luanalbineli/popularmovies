package com.themovielist.event

import com.themovielist.enums.MovieSortEnum

data class TabChangeFilterEvent(@MovieSortEnum.MovieSort val filter: Int)
