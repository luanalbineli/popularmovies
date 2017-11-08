package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.MovieCastModel
import com.themovielist.util.readNullableInt
import com.themovielist.util.writeNullableInt

data class MovieCastViewModel constructor(val movieId: Int? = null, var movieCastList: List<MovieCastModel>? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readNullableInt(),
            parcel.createTypedArrayList(MovieCastModel))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeNullableInt(movieId)
        parcel.writeTypedList(movieCastList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieCastViewModel> {
        override fun createFromParcel(parcel: Parcel): MovieCastViewModel {
            return MovieCastViewModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieCastViewModel?> {
            return arrayOfNulls(size)
        }
    }

}