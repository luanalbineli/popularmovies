package com.themovielist.enums

import android.support.annotation.IntDef

object RequestStatusDescriptor {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LOADING, ERROR, EMPTY, HIDDEN)
    annotation class RequestStatus

    const val LOADING = 0
    const val ERROR = 1
    const val EMPTY = 2
    const val HIDDEN = 3
}
