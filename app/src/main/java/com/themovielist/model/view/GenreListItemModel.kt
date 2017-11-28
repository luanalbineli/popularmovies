package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.GenreModel

data class GenreListItemModel constructor(val genreModel: GenreModel, var selected: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(GenreModel::class.java.classLoader),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(genreModel, flags)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GenreListItemModel> {
        override fun createFromParcel(parcel: Parcel): GenreListItemModel =
                GenreListItemModel(parcel)

        override fun newArray(size: Int): Array<GenreListItemModel?> = arrayOfNulls(size)
    }

}