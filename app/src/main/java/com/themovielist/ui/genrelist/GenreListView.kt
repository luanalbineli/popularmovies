package com.themovielist.ui.genrelist

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.themovielist.R
import com.themovielist.model.view.GenreListItemModel
import com.themovielist.ui.recyclerview.HorizonalSpaceItemDecoration
import kotlinx.android.synthetic.main.genre_list_view.view.*

class GenreListView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val mGenreListAdapter by lazy { GenreListAdapter(R.string.the_list_is_empty) }

    var onSelectGenreListener: ((position: Int, genreListItemModel: GenreListItemModel) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.genre_list_view, this)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        rvGenreList.adapter = mGenreListAdapter

        val dividerAmountOfSpace = context.resources.getDimension(R.dimen.genre_horizontal_space)
        val spaceItemViewDecoration = HorizonalSpaceItemDecoration(dividerAmountOfSpace.toInt())
        rvGenreList.addItemDecoration(spaceItemViewDecoration)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvGenreList.layoutManager = linearLayoutManager

        mGenreListAdapter.setOnItemClickListener { position, item ->
            item.selected = !item.selected
            mGenreListAdapter.notifyItemChanged(position)

            onSelectGenreListener?.invoke(position, item)
        }
    }

    fun showGenreList(genreListItemList: List<GenreListItemModel>) {
        mGenreListAdapter.addItems(genreListItemList)
    }
}