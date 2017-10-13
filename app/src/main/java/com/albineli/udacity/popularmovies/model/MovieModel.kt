package com.albineli.udacity.popularmovies.model

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

import com.albineli.udacity.popularmovies.repository.data.MovieContract
import com.google.gson.annotations.SerializedName

import java.util.Date

class MovieModel : Parcelable {

    private constructor(contentValues: ContentValues) {
        if (contentValues.containsKey(MovieContract.MovieEntry._ID)) {
            id = contentValues.getAsInteger(MovieContract.MovieEntry._ID)!!
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_POSTER_PATH)) {
            posterPath = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH)
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_OVERVIEW)) {
            overview = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_OVERVIEW)
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_TITLE)) {
            title = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE)
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)) {
            voteAverage = contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)!!
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
            releaseDate = Date(contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)!!)
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)) {
            backdropPath = contentValues.getAsString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)
        }

        if (contentValues.containsKey(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)) {
            voteCount = contentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)!!
        }
    }

    private constructor()

    constructor(cursor: Cursor) {
        id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID))
        posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH))
        overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW))
        title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE))
        voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE))
        releaseDate = Date(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)))
        backdropPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH))
        voteCount = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT))
    }

    private constructor(`in`: Parcel) {
        id = `in`.readInt()
        posterPath = `in`.readString()
        overview = `in`.readString()
        title = `in`.readString()
        voteAverage = `in`.readDouble()
        releaseDate = Date(`in`.readLong())
        backdropPath = `in`.readString()
        voteCount = `in`.readInt()
    }

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("poster_path")
    var posterPath: String? = null

    @SerializedName("overview")
    var overview: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("vote_average")
    var voteAverage: Double = 0.toDouble()

    @SerializedName("release_date")
    var releaseDate: Date? = null

    @SerializedName("backdrop_path")
    var backdropPath: String? = null

    @SerializedName("vote_count")
    var voteCount: Int = 0

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(posterPath)
        parcel.writeString(overview)
        parcel.writeString(title)

        parcel.writeDouble(voteAverage)
        parcel.writeLong(releaseDate!!.time)

        parcel.writeString(backdropPath)
        parcel.writeInt(voteCount)
    }

    fun toContentValues(): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(MovieContract.MovieEntry._ID, id)

        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath)

        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview)

        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title)

        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage)

        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate!!.time)

        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath)

        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount)

        return contentValues
    }

    companion object {

        val CREATOR: Parcelable.Creator<MovieModel> = object : Parcelable.Creator<MovieModel> {
            override fun createFromParcel(`in`: Parcel): MovieModel {
                return MovieModel(`in`)
            }

            override fun newArray(size: Int): Array<MovieModel> {
                return Array(size) { MovieModel() }
            }
        }

        fun fromContentValues(contentValues: ContentValues): MovieModel {
            return MovieModel(contentValues)
        }

        fun fromCursor(cursor: Cursor): MovieModel {
            return MovieModel(cursor)
        }
    }
}
