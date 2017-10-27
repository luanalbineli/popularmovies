package com.themovielist.moviedetail.review;

import com.themovielist.model.MovieReviewModel;
import com.themovielist.model.response.PaginatedArrayResponseModel;
import com.themovielist.repository.movie.MovieRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MovieReviewListDialogPresenter implements MovieReviewListDialogContract.Presenter {
    private final MovieRepository mMovieRepository;

    private MovieReviewListDialogContract.View mView;

    private int mPageIndex;
    private Disposable mSubscription;
    private int mMovieId;

    @Inject
    MovieReviewListDialogPresenter(MovieRepository movieRepository) {
        mMovieRepository = movieRepository;
    }

    @Override
    public void setView(MovieReviewListDialogContract.View view) {
        mView = view;
    }


    @Override
    public void start(List<MovieReviewModel> movieReviewList, int movieId, boolean hasMore) {
        mView.addReviewsToList(movieReviewList);
        if (hasMore) {
            mView.enableLoadMoreListener();
        }

        mMovieId = movieId;
    }

    @Override
    public void onListEndReached() {
        if (mSubscription != null) {
            return;
        }

        mView.showLoadingIndicator();

        mPageIndex++;

        Observable<PaginatedArrayResponseModel<MovieReviewModel>> observable = mMovieRepository.getReviewsByMovieId(mPageIndex, mMovieId);

        mSubscription = observable.subscribe(
                this::handleSuccessLoadMovieReview,
                this::handleErrorLoadMovieReview);
    }

    private void handleSuccessLoadMovieReview(PaginatedArrayResponseModel<MovieReviewModel> response) {
        mView.addReviewsToList(response.getResults());
        if (!response.hasMorePages()) {
            mView.disableLoadMoreListener();
        }
        mSubscription = null;
    }

    private void handleErrorLoadMovieReview(Throwable throwable) {
        Timber.e(throwable, "An error occurred while tried to get the movie reviews for page: " + mPageIndex);
        mPageIndex--;
        mView.showErrorLoadingReviews();
        mSubscription = null;
    }

    @Override
    public void tryLoadReviewsAgain() {
        onListEndReached();
    }
}
