package com.themovielist.ui.genrelist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter

internal class GenreListAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)? = null) : CustomRecyclerViewAdapter<GenreListItemModel, GenreListVH>(emptyMessageResId, tryAgainClickListener) {
    override fun onCreateItemViewHolder(parent: ViewGroup): GenreListVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.genre_item, parent, false)
        return GenreListVH(itemView)
    }

    override fun onBindItemViewHolder(holder: GenreListVH, position: Int) {
        val genreListItemModel = getItemByPosition(position)
        holder.bind(genreListItemModel)
    }
}