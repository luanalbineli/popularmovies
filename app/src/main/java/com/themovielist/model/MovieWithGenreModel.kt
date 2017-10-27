package com.themovielist.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.themovielist.util.readIntArray
import java.util.*

class MovieWithGenreModel constructor(id: Int = 0, posterPath: String, overview: String,
                                      title: String, voteAverage: Double, releaseDate: Date? = null,
                                      backdropPath: String, voteCount: Int,
                                      @SerializedName("genre_ids")
                                      val genreIdList: IntArray) :
        MovieModel(id, posterPath, overview, title, voteAverage, releaseDate, backdropPath, voteCount) {

    @Transient
    var genreList: List<GenreModel>? = null

    constructor(parcel: Parcel) : this(parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            Date(parcel.readLong()),
            parcel.readString(),
            parcel.readInt(),
            parcel.readIntArray()) {

        val genreListSize = parcel.readInt()
        if (genreListSize > 0) {
            genreList = mutableListOf<GenreModel>().also {
                it.addAll(parcel.createTypedArray(GenreModel.CREATOR))
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeIntArray(genreIdList)
        parcel.writeInt(genreList?.size ?: 0)
        genreList?.let {
            parcel.writeParcelableArray(it.toTypedArray(), 0)
        }
    }

    companion object CREATOR : Parcelable.Creator<MovieWithGenreModel> {
        override fun createFromParcel(parcel: Parcel): MovieWithGenreModel {
            return MovieWithGenreModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieWithGenreModel?> {
            return arrayOfNulls(size)
        }
    }
}