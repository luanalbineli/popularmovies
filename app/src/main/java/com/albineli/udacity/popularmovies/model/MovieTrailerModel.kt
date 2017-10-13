package com.albineli.udacity.popularmovies.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

class MovieTrailerModel constructor(@SerializedName("site") val site: String,
                                    @SerializedName("size") val size: Int,
                                    @SerializedName("key") val key: String,
                                    @SerializedName("name") val name: String) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readString(), parcel.readInt(), parcel.readString(), parcel.readString())

    private constructor() : this("", 0, "", "")

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(site)
        dest.writeInt(size)
        dest.writeString(key)
        dest.writeString(name)
    }

    override fun toString(): String {
        return "(site = $site, size = $size, key = $key, name = $name)"
    }

    companion object {

        val CREATOR: Parcelable.Creator<MovieTrailerModel> = object : Parcelable.Creator<MovieTrailerModel> {
            override fun createFromParcel(parcel: Parcel): MovieTrailerModel {
                return MovieTrailerModel(parcel)
            }

            override fun newArray(size: Int): Array<MovieTrailerModel> {
                return Array(size) { MovieTrailerModel() }
            }
        }
    }
}
