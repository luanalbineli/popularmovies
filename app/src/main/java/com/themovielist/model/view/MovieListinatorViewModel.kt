package com.themovielist.model.view

import android.os.Bundle
import com.themovielist.model.MovieModel
import java.util.*

data class MovieListinatorViewModel constructor(val movieList: List<MovieModel>? = null, val firstVisibleMovieIndex: Int? = null) {

    companion object {
        fun saveToBundle(bundle: Bundle, movieList: List<MovieModel>, firstVisibleMovieIndex: Int) {
            bundle.putParcelableArrayList(MOVIE_LIST_BUNDLE_KEY, ArrayList(movieList))
            bundle.putInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, firstVisibleMovieIndex)
        }

        fun getFromBundle(bundle: Bundle): MovieListinatorViewModel {
            val firstVisibleMovieIndex = bundle.getInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE)
            val movieList = bundle.getParcelableArrayList<MovieModel>(MOVIE_LIST_BUNDLE_KEY)
            return MovieListinatorViewModel(movieList, firstVisibleMovieIndex)
        }

        private val MOVIE_LIST_BUNDLE_KEY = "movie_list_bundle_key"
        private val FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY = "first_visible_movie_index_bundle_key"
    }
}