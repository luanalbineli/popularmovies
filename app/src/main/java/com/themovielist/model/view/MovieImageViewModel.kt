package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.MovieModel

open class MovieImageViewModel(val movieModel: MovieModel, var isFavorite: Boolean): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(MovieModel::class.java.classLoader),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(movieModel, flags)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieImageViewModel> {
        override fun createFromParcel(parcel: Parcel): MovieImageViewModel {
            return MovieImageViewModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieImageViewModel?> {
            return arrayOfNulls(size)
        }
    }
}