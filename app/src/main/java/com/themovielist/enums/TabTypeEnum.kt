package com.themovielist.enums

import android.support.annotation.IntDef

object TabTypeEnum {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(HOME.toLong(), BROWSE.toLong(), CINEMA.toLong(), FAVORITE.toLong())
    annotation class TabType

    const val HOME = 0
    const val BROWSE = 1
    const val CINEMA = 2
    const val FAVORITE = 3
}