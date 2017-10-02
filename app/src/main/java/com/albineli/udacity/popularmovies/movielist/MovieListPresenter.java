package com.albineli.udacity.popularmovies.movielist;

import android.support.annotation.NonNull;

import com.albineli.udacity.popularmovies.base.BasePresenterImpl;
import com.albineli.udacity.popularmovies.enums.MovieListFilterDescriptor;
import com.albineli.udacity.popularmovies.model.MovieListStateModel;
import com.albineli.udacity.popularmovies.model.MovieModel;
import com.albineli.udacity.popularmovies.repository.ArrayRequestAPI;
import com.albineli.udacity.popularmovies.repository.movie.MovieRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Presenter of the movie list fragment.
 */

public class MovieListPresenter extends BasePresenterImpl implements MovieListContract.Presenter {
    private MovieListContract.View mView;
    private @MovieListFilterDescriptor.MovieListFilter int mFilter;

    private boolean mHasError = false;
    private Disposable mSubscription;
    /*
        The unitial page must be 1 (API implementation).
     */
    int pageIndex = 1;
    int selectedMovieIndex = Integer.MIN_VALUE;

    @Inject
    MovieListPresenter(@NonNull MovieRepository movieRepository) {
        super(movieRepository);
    }

    @Override
    public void setView(MovieListContract.View view) {
        mView = view;
    }

    @Override
    public void init(@MovieListFilterDescriptor.MovieListFilter int filter, MovieListStateModel movieListStateModel) {
        if (movieListStateModel == null || movieListStateModel.movieList == null) { // Handle a invalid state restore.
            loadMovieList(true);
            return;
        }

        Timber.i("Restoring the state - pageIndex: " + movieListStateModel.pageIndex +
                "\nselectedMovieIndex: " + movieListStateModel.selectedMovieIndex +
                "\nfirst visible item index: " + movieListStateModel.firstVisibleMovieIndex);

        handleSuccessLoadMovieList(movieListStateModel.movieList, true, true);
        if (movieListStateModel.selectedMovieIndex != Integer.MIN_VALUE) {
            selectedMovieIndex = movieListStateModel.selectedMovieIndex;
            mView.showMovieDetail(movieListStateModel.movieList.get(movieListStateModel.selectedMovieIndex));
        }

        if (movieListStateModel.firstVisibleMovieIndex != Integer.MIN_VALUE) {
            mView.scrollToMovieIndex(movieListStateModel.firstVisibleMovieIndex);
        }

        pageIndex = movieListStateModel.pageIndex;
    }

    @Override
    public void onStop() {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.dispose();
        }
    }

    private void loadMovieList(final boolean startOver, @MovieListFilterDescriptor.MovieListFilter int filter) {
        Timber.i("loadMovieList - Loading the movie list");
        if (mSubscription != null) {
            return;
        }

        mView.showLoadingIndicator();

        if (startOver) {
            pageIndex = 1;
        } else {
            pageIndex++;
        }

        Observable<ArrayRequestAPI<MovieModel>> observable;
        if (filter == MovieListFilterDescriptor.POPULAR) {
            observable = mMovieRepository.getPopularList(pageIndex);
        } else if (filter == MovieListFilterDescriptor.RATING) {
            observable = mMovieRepository.getTopRatedList(pageIndex);
        } else {
            observable = mMovieRepository.getFavoriteList();
        }

        mSubscription = observable
                .doOnTerminate(() -> mSubscription = null)
                .subscribe(
                    response -> handleSuccessLoadMovieList(response.results, response.hasMorePages(), startOver),
                    this::handleErrorLoadMovieList);
    }

    private void handleSuccessLoadMovieList(List<MovieModel> movieList, boolean hasMorePages, boolean startOver) {
        Timber.i("handleSuccessLoadMovieList - CHANGED");
        if (movieList.size() == 0) {
            mView.clearMovieList(); // Make sure that the list is empty.
            mView.showEmptyListMessage();
        } else {
            mView.showMovieList(movieList, startOver);
        }

        if (hasMorePages) {
            mView.enableLoadMoreListener();
        } else {
            mView.disableLoadMoreListener();
        }
    }

    private void handleErrorLoadMovieList(Throwable throwable) {
        mHasError = true;
        Timber.e(throwable, "An error occurred while tried to get the movies");

        if (pageIndex > 1) { // If something got wrong, reverse to the original position.
            pageIndex--;
        }

        if (mFilter == MovieListFilterDescriptor.FAVORITE) {
            mView.clearMovieList();
        }

        mView.showLoadingMovieListError();
    }

    @Override
    public void loadMovieList(final boolean startOver) {
        loadMovieList(startOver, mFilter);
    }

    @Override
    public void setFilter(@MovieListFilterDescriptor.MovieListFilter int movieListFilter) {
        if (mFilter == movieListFilter) { // If it's the same order, do nothing.
            return;
        }

        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.dispose();
            mSubscription = null;
        }

        mFilter = movieListFilter;
        mView.clearMovieList();

        // Reload the movie list.
        loadMovieList(true);

        mView.setTitleByFilter(movieListFilter);
    }

    @Override
    public void openMovieDetail(int selectedMovieIndex, MovieModel movieModel) {
        this.selectedMovieIndex = selectedMovieIndex;
        mView.showMovieDetail(movieModel);
    }

    @Override
    public void onListEndReached() {
        if (mHasError) {
            return;
        }
        loadMovieList(false);
    }

    @Override
    public void tryAgain() {
        mHasError = false;
        mView.hideRequestStatus();

        loadMovieList(false);
    }

    @Override
    public void favoriteMovie(MovieModel movie, boolean favorite) {
        if (mFilter != MovieListFilterDescriptor.FAVORITE) { // Only if the user is at favorite list it needs an update.
            return;
        }

        if (favorite) {
            mView.addMovieToListByIndex(selectedMovieIndex, movie);
        } else {
            mView.removeMovieFromListByIndex(selectedMovieIndex);
        }

        if (mView.getMovieListCount() == 0) {
            mView.showEmptyListMessage();
        }
    }

    @Override
    public void resume() {
        mView.setTitleByFilter(mFilter);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            mView.setTitleByFilter(mFilter);
        }
    }
}
