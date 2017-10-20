package com.themovielist.ui.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View


open class CustomRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val context: Context
        get() = itemView.context
}
