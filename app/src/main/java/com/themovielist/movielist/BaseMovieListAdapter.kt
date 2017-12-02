package com.themovielist.movielist

import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.model.view.MovieImageGenreViewModel
import com.themovielist.ui.recyclerview.CustomRecyclerViewAdapter

internal abstract class BaseMovieListAdapter(emptyMessageResId: Int, tryAgainClickListener: (() -> Unit)?) : CustomRecyclerViewAdapter<MovieImageGenreViewModel, MovieListVH>(emptyMessageResId, tryAgainClickListener) {
    lateinit var configurationImageModel: ConfigurationImageResponseModel
}
