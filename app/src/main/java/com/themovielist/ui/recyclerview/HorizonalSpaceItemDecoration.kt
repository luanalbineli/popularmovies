package com.themovielist.ui.recyclerview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class HorizonalSpaceItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1) {
            outRect.right = spaceHeight
        }
    }
}
