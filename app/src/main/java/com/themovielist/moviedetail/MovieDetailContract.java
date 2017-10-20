package com.themovielist.moviedetail;

import com.themovielist.base.BasePresenter;
import com.themovielist.model.MovieModel;
import com.themovielist.model.MovieReviewModel;
import com.themovielist.model.MovieTrailerModel;

import java.util.List;

/**
 * Presenter of the Movie Detail Fragment.
 */

public abstract class MovieDetailContract {
    public interface View {
        void showMovieReview(List<MovieReviewModel> movieReviewModelList);

        void showMovieDetail(MovieModel movieModel);

        void showMovieTrailer(List<MovieTrailerModel> movieTrailerList);

        void setFavoriteButtonState(boolean favorite);

        void showSuccessMessageAddFavoriteMovie();

        void showSuccessMessageRemoveFavoriteMovie();


        void showErrorMessageAddFavoriteMovie();

        void showErrorMessageRemoveFavoriteMovie();

        void showErrorMessageLoadReviews();

        void showErrorMessageLoadTrailers();

        void setShowAllReviewsButtonVisibility(boolean visible);

        void setShowAllTrailersButtonVisibility(boolean visible);

        void showLoadingReviewsIndicator();

        void showLoadingTrailersIndicator();

        void showAllReviews(List<MovieReviewModel> movieReviewList, boolean hasMore);

        void showAllTrailers(List<MovieTrailerModel> movieTrailerList);

        void showEmptyReviewListMessage();

        void showEmptyTrailerListMessage();
    }

    public interface Presenter extends BasePresenter<View> {
        void start(MovieModel movieModel);

        void removeFavoriteMovie(MovieModel movieModel);

        void saveFavoriteMovie(MovieModel movieModel);

        void showAllReviews();

        void showAllTrailers();

        void tryToLoadTrailersAgain();

        void tryToLoadReviewAgain();
    }
}
