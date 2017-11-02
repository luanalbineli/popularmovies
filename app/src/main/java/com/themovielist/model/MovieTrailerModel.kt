package com.themovielist.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

data class MovieTrailerModel constructor(@SerializedName("size") val size: String,
                                         @SerializedName("source") val source: String,
                                         @SerializedName("name") val name: String) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(size)
        dest.writeString(source)
        dest.writeString(name)
    }

    companion object {
        val CREATOR: Parcelable.Creator<MovieTrailerModel> = object : Parcelable.Creator<MovieTrailerModel> {
            override fun createFromParcel(parcel: Parcel): MovieTrailerModel {
                return MovieTrailerModel(parcel)
            }

            override fun newArray(size: Int): Array<MovieTrailerModel?> {
                return kotlin.arrayOfNulls(size)
            }
        }
    }
}
