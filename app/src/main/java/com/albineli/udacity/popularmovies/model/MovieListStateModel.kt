package com.albineli.udacity.popularmovies.model

import android.os.Bundle

import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor

import java.util.ArrayList

data class MovieListStateModel constructor(val movieList: List<MovieModel>, @MovieListFilterDescriptor.MovieListFilter val filter: Int, val pageIndex: Int, val selectedMovieIndex: Int, val firstVisibleMovieIndex: Int) {

    companion object {
        fun saveToBundle(bundle: Bundle, movieList: List<MovieModel>, filter: Int, pageIndex: Int, selectedMovieIndex: Int, firstVisibleMovieIndex: Int) {
            bundle.putParcelableArrayList(MOVIE_LIST_BUNDLE_KEY, ArrayList(movieList))
            bundle.putInt(PAGE_INDEX_BUNDLE_KEY, pageIndex)
            bundle.putInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, selectedMovieIndex)
            bundle.putInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, firstVisibleMovieIndex)
            bundle.putInt(FILTER_BUNDLE_KEY, filter)
        }

        fun getFromBundle(bundle: Bundle?): MovieListStateModel? {
            if (bundle == null) {
                return null
            }

            @MovieListFilterDescriptor.MovieListFilter val filter = bundle.getInt(FILTER_BUNDLE_KEY)
            val pageIndex = bundle.getInt(PAGE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE)
            val selectedMovieIndex = bundle.getInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE)
            val firstVisibleMovieIndex = bundle.getInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE)
            val movieList = bundle.getParcelableArrayList<MovieModel>(MOVIE_LIST_BUNDLE_KEY)

            return MovieListStateModel(movieList, filter, pageIndex, selectedMovieIndex, firstVisibleMovieIndex)
        }

        fun getFromArguments(arguments: Bundle): MovieListStateModel {
            val filter = MovieListFilterDescriptor.parseFromInt(arguments.getInt(FILTER_BUNDLE_KEY))
            return MovieListStateModel(emptyList(), filter, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)
        }

        private val SELECTED_MOVIE_INDEX_BUNDLE_KEY = "selected_movie_index_bundle_key"
        private val FILTER_BUNDLE_KEY = "filter_bundle_key"
        private val PAGE_INDEX_BUNDLE_KEY = "page_index_bundle_key"
        private val MOVIE_LIST_BUNDLE_KEY = "movie_list_bundle_key"
        private val FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY = "first_visible_movie_index_bundle_key"
    }
}
