package com.themovielist.enums

import android.support.annotation.IntDef

object RequestStatusDescriptor {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LOADING, ERROR, EMPTY, HIDDEN)
    annotation class RequestStatus

    const val LOADING = 0L
    const val ERROR = 1L
    const val EMPTY = 2L
    const val HIDDEN = 3L
}
