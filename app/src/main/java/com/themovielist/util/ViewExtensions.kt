package com.themovielist.util

import android.view.View

fun View.setDisplay(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}