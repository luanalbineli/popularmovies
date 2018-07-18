package com.themovielist.model

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.enums.ImageSizeEnum

data class MovieSizeModel constructor(val size: Int, @ImageSizeEnum.ImageSize val sizeType: Long) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(size)
        parcel.writeLong(sizeType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieSizeModel> {
        override fun createFromParcel(parcel: Parcel): MovieSizeModel {
            return MovieSizeModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieSizeModel?> {
            return arrayOfNulls(size)
        }
    }

}