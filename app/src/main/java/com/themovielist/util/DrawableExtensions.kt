package com.themovielist.util

import android.graphics.LightingColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt


fun Drawable.tintSupport(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.setTint(color)
    } else {
        this.colorFilter = LightingColorFilter(color, color)
    }
}