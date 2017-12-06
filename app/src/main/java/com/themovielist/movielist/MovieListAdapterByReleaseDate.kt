package com.themovielist.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.themovielist.R
import com.themovielist.enums.MovieGroupedByViewTypeEnum
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieGroupedByReleaseDateItemModel
import com.themovielist.model.view.MovieGroupedByReleaseDateModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter
import com.themovielist.ui.recyclerview.CustomRecyclerViewHolder

class MovieListAdapterByReleaseDate: CustomRecyclerViewAdapter<MovieGroupedByReleaseDateModel, CustomRecyclerViewHolder>() {
    lateinit var configurationImageModel: ConfigurationImageResponseModel

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): CustomRecyclerViewHolder {
        if (viewType == MovieGroupedByViewTypeEnum.ITEM) {
            return MovieListVH(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))
        }
        return HeaderVH(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))
    }

    override fun onBindItemViewHolder(holder: CustomRecyclerViewHolder, position: Int) {
        if (holder.itemViewType == MovieGroupedByViewTypeEnum.ITEM) {
            val itemModel = getItemByPosition(position) as MovieGroupedByReleaseDateItemModel
            (holder as MovieListVH).bind(itemModel.movieImageGenreViewModel, configurationImageModel)
        } else {
            (holder as HeaderVH).bind(getItemByPosition(position))
        }
    }


    override fun getItemViewTypeOverride(position: Int) = getItemByPosition(position).type
}