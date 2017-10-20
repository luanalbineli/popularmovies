package com.themovielist.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class NonScrollableLLM(context: Context, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {

    // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
    override fun canScrollVertically(): Boolean {
        return false
    }
}
