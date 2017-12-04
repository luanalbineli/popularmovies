package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.util.Defaults

data class FavoriteMovieViewModel(var favoriteMovieList: List<MovieImageGenreViewModel>, var movieListSort: Int = Defaults.FAVORITE_LIST_SORT): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(MovieImageGenreViewModel),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(favoriteMovieList)
        parcel.writeInt(movieListSort)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteMovieViewModel> {
        override fun createFromParcel(parcel: Parcel): FavoriteMovieViewModel {
            return FavoriteMovieViewModel(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteMovieViewModel?> {
            return arrayOfNulls(size)
        }
    }

}