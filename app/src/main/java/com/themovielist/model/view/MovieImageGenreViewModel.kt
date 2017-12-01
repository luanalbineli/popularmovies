package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.GenreModel
import com.themovielist.model.MovieModel
import com.themovielist.util.readBoolean

class MovieImageGenreViewModel(val genreList: List<GenreModel>?, movieModel: MovieModel, isFavorite: Boolean) : MovieImageViewModel(movieModel, isFavorite) {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(GenreModel),
            parcel.readParcelable<MovieModel>(MovieModel::class.java.classLoader),
            parcel.readBoolean()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(genreList)
        parcel.writeParcelable(movieModel, flags)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieImageGenreViewModel> {
        override fun createFromParcel(parcel: Parcel): MovieImageGenreViewModel {
            return MovieImageGenreViewModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieImageGenreViewModel?> {
            return arrayOfNulls(size)
        }
    }
}