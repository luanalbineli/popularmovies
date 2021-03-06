package com.themovielist.ui.genrelist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.repository.movie.CommonRepository
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder
import kotlinx.android.synthetic.main.genre_item.view.*


class GenreListVH(itemView: View)
    : CustomRecyclerViewHolder(itemView) {

    fun bind(genreModel: GenreListItemModel) {
        itemView.tvGenreName.text = genreModel.genreModel.name

        val color = if (genreModel.selected)
            Color.parseColor(CommonRepository.GENRE_COLOR_MAP.get(genreModel.genreModel.id))
        else
            ContextCompat.getColor(itemView.context, android.R.color.darker_gray)

        val background = itemView.tvGenreName.background
        when (background) {
            is ShapeDrawable -> background.paint.color = color
            is GradientDrawable -> background.setColor(color)
            is ColorDrawable -> background.color = color
        }
    }
}
