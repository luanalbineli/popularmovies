package com.themovielist.enums

import android.support.annotation.IntDef

object ImageSizeEnum {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(WIDTH, HEIGHT)
    annotation class ImageSize

    const val WIDTH = 0L
    const val HEIGHT = 1L
}