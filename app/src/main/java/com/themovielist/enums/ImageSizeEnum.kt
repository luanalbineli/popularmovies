package com.themovielist.enums

import android.support.annotation.IntDef

object ImageSizeEnum {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(WIDTH.toLong(), HEIGHT.toLong())
    annotation class ImageSize

    const val WIDTH = 0
    const val HEIGHT = 1
}