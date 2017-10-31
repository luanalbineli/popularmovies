package com.themovielist.model

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.themovielist.repository.data.MovieContract
import com.themovielist.util.*
import java.util.*

open class MovieModel constructor(@SerializedName("id")
                                  var id: Int = 0,

                                  @SerializedName("poster_path")
                                  var posterPath: String = "",

                                  @SerializedName("overview")
                                  var overview: String = "",

                                  @SerializedName("title")
                                  var title: String = "",

                                  @SerializedName("vote_average")
                                  var voteAverage: Double = 0.0,

                                  @SerializedName("release_date")
                                  var releaseDate: Date? = null,

                                  @SerializedName("backdrop_path")
                                  var backdropPath: String = "",

                                  @SerializedName("vote_count")
                                  var voteCount: Int = 0,

                                  @SerializedName("genre_ids")
                                  val genreIdList: IntArray) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            Date(parcel.readLong()),
            parcel.readString(),
            parcel.readInt(),
            parcel.readIntArray())

    private constructor(contentValues: ContentValues) : this(
            contentValues.getAsInteger(MovieContract.MovieEntry._ID),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_OVERVIEW),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_TITLE),
            contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE),
            Date(contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
            contentValues.getAsString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH),
            contentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_VOTE_COUNT),
            contentValues.getAsIntArray(MovieContract.MovieEntry.COLUMN_GENRE_ID_LIST))

    private constructor(cursor: Cursor) : this(
            cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
            cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
            Date(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))),
            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)),
            cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT)),
            cursor.getIntArray(MovieContract.MovieEntry.COLUMN_VOTE_COUNT))

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

        contentValues.put(MovieContract.MovieEntry.COLUMN_GENRE_ID_LIST, genreIdList)

        return contentValues
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(posterPath)
        parcel.writeString(overview)
        parcel.writeString(title)
        parcel.writeDouble(voteAverage)
        parcel.writeLong(releaseDate!!.time)
        parcel.writeString(backdropPath)
        parcel.writeInt(voteCount)
        parcel.writeIntArrayWithLength(genreIdList)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || !MovieModel::class.java.isAssignableFrom(other.javaClass)) {
            return false
        }

        return id == (other as MovieModel).id
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<MovieModel> {
        @JvmField
        val EMPTY_MOVIE = MovieModel(Int.MIN_VALUE, genreIdList = IntArray(0))

        override fun createFromParcel(parcel: Parcel): MovieModel {
            return MovieModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieModel?> {
            return arrayOfNulls(size)
        }

        fun fromCursor(cursor: Cursor): MovieModel {
            return MovieModel(cursor)
        }
    }
}
