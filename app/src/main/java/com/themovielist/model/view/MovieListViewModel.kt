package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.MovieModel

data class MovieListViewModel(var movieList: List<MovieModel>? = null): Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(MovieModel))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(movieList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MovieListViewModel> {
        override fun createFromParcel(parcel: Parcel): MovieListViewModel =
                MovieListViewModel(parcel)

        override fun newArray(size: Int): Array<MovieListViewModel?> = arrayOfNulls(size)
    }
}