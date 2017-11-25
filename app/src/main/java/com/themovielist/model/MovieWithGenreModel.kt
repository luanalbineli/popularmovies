package com.themovielist.model

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.util.readIntArray
import java.util.*

class MovieWithGenreModel(id: Int = 0, posterPath: String, overview: String,
                          title: String, voteAverage: Double, releaseDate: Date? = null,
                          backdropPath: String, voteCount: Int, genreIdList: IntArray) :
        MovieModel(id, posterPath, overview, title, voteAverage, releaseDate, backdropPath, voteCount, genreIdList) {

    @Transient
    var genreList: List<GenreModel>? = null

    constructor(movieModel: MovieModel, genreList: List<GenreModel>) : this(movieModel.id,
            movieModel.posterPath,
            movieModel.overview,
            movieModel.title,
            movieModel.voteAverage,
            movieModel.releaseDate,
            movieModel.backdropPath,
            movieModel.voteCount,
            movieModel.genreIdList) {
        this.genreList = genreList
    }

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

    fun concattedGenres(): CharSequence? =
            genreList?.map { it.name }?.reduce { a, b -> "$a, $b"} ?: ""
}