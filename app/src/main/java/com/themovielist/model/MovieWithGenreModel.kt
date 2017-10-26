package com.themovielist.model

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.themovielist.repository.data.MovieContract
import com.themovielist.util.readIntArray
import java.util.*

class MovieWithGenreModel constructor(id: Int = 0, posterPath: String, overview: String,
                                      title: String, voteAverage: Double, releaseDate: Date? = null,
                                      backdropPath: String, voteCount: Int,
                                      @SerializedName("genre_ids") val genreIdList: IntArray) :
        MovieModel(id, posterPath, overview, title, voteAverage, releaseDate, backdropPath, voteCount) {



    constructor(parcel: Parcel) : this(parcel)

    private constructor(contentValues: ContentValues) : this(
            contentValues.getAsInteger(MovieContract.MovieEntry._ID),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_OVERVIEW),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE),
            contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE),
            Date(contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH),
            contentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_VOTE_COUNT))

    constructor(cursor: Cursor) : this(
            cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
            cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
            Date(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)),
            cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)))


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)

    }

    companion object CREATOR : Parcelable.Creator<MovieWithGenreModel> {
        @JvmField
        val EMPTY_MOVIE = MovieModel(Int.MIN_VALUE)

        override fun createFromParcel(parcel: Parcel): MovieWithGenreModel {
            return MovieWithGenreModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieWithGenreModel?> {
            return arrayOfNulls(size)
        }

        fun fromContentValues(contentValues: ContentValues): MovieWithGenreModel {
            return MovieWithGenreModel(contentValues)
        }

        fun fromCursor(cursor: Cursor): MovieWithGenreModel {
            return MovieWithGenreModel(cursor)
        }
    }
}