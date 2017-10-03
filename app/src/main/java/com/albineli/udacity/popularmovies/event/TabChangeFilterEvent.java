package com.albineli.udacity.popularmovies.event;

import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor;

public class TabChangeFilterEvent {
    public final @MovieListFilterDescriptor.MovieListFilter int filter;

    public TabChangeFilterEvent(@MovieListFilterDescriptor.MovieListFilter int filter) {
        this.filter = filter;
    }
}
