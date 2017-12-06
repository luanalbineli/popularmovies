package com.themovielist.model.view

import com.themovielist.enums.MovieGroupedByViewTypeEnum
import java.util.*

open class MovieGroupedByReleaseDateModel(val type: Int = MovieGroupedByViewTypeEnum.HEADER, val releaseDate: Date? = null)
