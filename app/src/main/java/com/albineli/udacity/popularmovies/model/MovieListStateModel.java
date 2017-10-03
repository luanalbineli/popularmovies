package com.albineli.udacity.popularmovies.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieListStateModel {
    public final int pageIndex;
    public final @MovieListFilterDescriptor.MovieListFilter int filter;
    public final List<MovieModel> movieList;
    public final int selectedMovieIndex;
    public final int firstVisibleMovieIndex;

    private MovieListStateModel(List<MovieModel> movieList, @MovieListFilterDescriptor.MovieListFilter int filter, int pageIndex, int selectedMovieIndex, int firstVisibleMovieIndex) {
        this.movieList = movieList;
        this.filter = filter;
        this.pageIndex = pageIndex;
        this.selectedMovieIndex = selectedMovieIndex;
        this.firstVisibleMovieIndex = firstVisibleMovieIndex;
    }

    public static void saveToBundle(Bundle bundle, List<MovieModel> movieList, int filter, int pageIndex, int selectedMovieIndex, int firstVisibleMovieIndex) {
        bundle.putParcelableArrayList(MOVIE_LIST_BUNDLE_KEY, new ArrayList<>(movieList));
        bundle.putInt(PAGE_INDEX_BUNDLE_KEY, pageIndex);
        bundle.putInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, selectedMovieIndex);
        bundle.putInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, firstVisibleMovieIndex);
        bundle.putInt(FILTER_BUNDLE_KEY, filter);
    }

    public static @Nullable MovieListStateModel getFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        @MovieListFilterDescriptor.MovieListFilter int filter = bundle.getInt(FILTER_BUNDLE_KEY);
        int pageIndex = bundle.getInt(PAGE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        int selectedMovieIndex = bundle.getInt(SELECTED_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        int firstVisibleMovieIndex = bundle.getInt(FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY, Integer.MIN_VALUE);
        ArrayList<MovieModel> movieList = bundle.getParcelableArrayList(MOVIE_LIST_BUNDLE_KEY);

        return new MovieListStateModel(movieList, filter, pageIndex, selectedMovieIndex, firstVisibleMovieIndex);
    }

    public static @NotNull MovieListStateModel getFromArguments(Bundle arguments) {
        int filter = MovieListFilterDescriptor.parseFromInt(arguments.getInt(FILTER_BUNDLE_KEY));
        return new MovieListStateModel(null, filter, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    private static String SELECTED_MOVIE_INDEX_BUNDLE_KEY = "selected_movie_index_bundle_key";
    private static String FILTER_BUNDLE_KEY = "filter_bundle_key";
    private static String PAGE_INDEX_BUNDLE_KEY = "page_index_bundle_key";
    private static String MOVIE_LIST_BUNDLE_KEY = "movie_list_bundle_key";
    private static String FIRST_VISIBLE_MOVIE_INDEX_BUNDLE_KEY = "first_visible_movie_index_bundle_key";
}
