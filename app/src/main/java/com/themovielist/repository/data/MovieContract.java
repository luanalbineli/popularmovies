package com.themovielist.repository.data;

import android.net.Uri;
import android.provider.BaseColumns;

public abstract class MovieContract {
    static final String CONTENT_AUTHORITY = "com.albineli.udacity.popuparmovies.provider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie_table";

        public static final String COLUMN_POSTER_PATH = "posterPath";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final String COLUMN_BACKDROP_PATH = "backdropPath";

        public static final String COLUMN_GENRE_ID_LIST = "genreIds";

        public static Uri buildMovieWithId(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(id))
                    .build();
        }
    }
}
