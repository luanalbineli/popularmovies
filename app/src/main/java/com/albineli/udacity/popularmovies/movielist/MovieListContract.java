package com.albineli.udacity.popularmovies.movielist;

import com.albineli.udacity.popularmovies.base.BasePresenter;
import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor;
import com.albineli.udacity.popularmovies.model.MovieListStateModel;
import com.albineli.udacity.popularmovies.model.MovieModel;

import java.util.List;

interface MovieListContract {
    interface View {
        void setTitleByFilter(@MovieListFilterDescriptor.MovieListFilter int filter);

        void showLoadingMovieListError();
        void showMovieList(List<MovieModel> movieList, boolean replaceData);
        void showMovieDetail(MovieModel movieModel);

        void clearMovieList();

        void showEmptyListMessage();
        void hideRequestStatus();

        void showLoadingIndicator();

        void enableLoadMoreListener();

        void disableLoadMoreListener();

        void removeMovieFromListByIndex(int index);

        void addMovieToListByIndex(int index, MovieModel movieModel);

        int getMovieListCount();

        void scrollToMovieIndex(int firstVisibleMovieIndex);
    }

    interface Presenter extends BasePresenter<View> {
        void init(MovieListStateModel movieListStateModel);
        void onStop();
        void loadMovieList(boolean startOver);
        void changeFilterList(@MovieListFilterDescriptor.MovieListFilter int filter);

        void openMovieDetail(int selectedMovieIndex, MovieModel movieModel);

        void onListEndReached();

        void tryAgain();

        void favoriteMovie(MovieModel movie, boolean favorite);

        void onVisibilityChanged(boolean visible);
    }
}
