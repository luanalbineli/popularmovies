package com.albineli.udacity.popularmovies.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MovieListStateModel {
    public final int pageIndex;
    public final List<MovieModel> movieList;
    public final int selectedMovieIndex;
    public final int firstVisibleMovieIndex;

    private MovieListStateModel(List<MovieModel> movieList, int pageIndex, int selectedMovieIndex, int firstVisibleMovieIndex) {
        this.movieList = movieList;
        this.pageIndex = pageIndex;
        this.selectedMovieIndex = selectedMovieIndex;
        this.firstVisibleMovieIndex = firstVisibleMovieIndex;
    }

    public static void saveToBundle(Bundle bundle, List<MovieModel> movieList, int pageIndex, int selectedMovieIndex, int firstVisibleMovieIndex) {
        bundle.putParcelableArrayList(MOVIE_LIST_BUNDLE_KEY, new ArrayList<>(movieList));
        bundle.putInt(PAGE_INDEX_BUNDLE_KEY, pageIndex);
        bundle.putInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, selectedMovieIndex);
        bundle.putInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, firstVisibleMovieIndex);
    }

    public static @Nullable MovieListStateModel getFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        int pageIndex = bundle.getInt(PAGE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        int selectedMovieIndex = bundle.getInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        int firstVisibleMovieIndex = bundle.getInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        ArrayList<MovieModel> movieList = bundle.getParcelableArrayList(MOVIE_LIST_BUNDLE_KEY);

        return new MovieListStateModel(movieList, pageIndex, selectedMovieIndex, firstVisibleMovieIndex);
    }

    private static String SELECTED_MOVIE_INDEX_BUNDLE_KEY = "selected_movie_index_bundle_key";
    private static String PAGE_INDEX_BUNDLE_KEY = "page_index_bundle_key";
    private static String MOVIE_LIST_BUNDLE_KEY = "movie_list_bundle_key";
    private static String FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY = "first_visible_movie_index_bundle_key";
}
