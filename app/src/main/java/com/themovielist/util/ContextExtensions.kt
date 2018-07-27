package com.themovielist.util

import android.content.Context
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager




fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.getScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val screenSize = Point()
    display.getSize(screenSize)

    return screenSize
}