package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.MovieModel

data class HomeViewModel(var movieListByPopularity: List<MovieModel>? = null,
                         var movieListByRating: List<MovieModel>? = null): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(MovieModel),
            parcel.createTypedArrayList(MovieModel))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(movieListByPopularity)
        parcel.writeTypedList(movieListByRating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeViewModel> {
        override fun createFromParcel(parcel: Parcel): HomeViewModel {
            return HomeViewModel(parcel)
        }

        override fun newArray(size: Int): Array<HomeViewModel?> {
            return arrayOfNulls(size)
        }
    }

}