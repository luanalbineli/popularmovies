package com.themovielist.enums

import android.support.annotation.IntDef
import java.security.InvalidParameterException

object MovieSortEnum {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(POPULAR.toLong(), RATING.toLong(), FAVORITE.toLong())
    annotation class MovieSort

    const val POPULAR = 0
    const val RATING = 1
    const val FAVORITE = 2

    @MovieSortEnum.MovieSort
    fun parseFromInt(supposedMovieListFilter: Int): Int {
        when (supposedMovieListFilter) {
            POPULAR -> return POPULAR
            RATING -> return RATING
            FAVORITE -> return FAVORITE
        }
        throw InvalidParameterException("supposedMovieListFilter")
    }
}
