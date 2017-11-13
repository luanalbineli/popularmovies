package com.themovielist.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MovieCastModel constructor(@SerializedName("character") val character: String,
                                      @SerializedName("name") val name: String,
                                      @SerializedName("profile_path") val profilePath: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(character)
        parcel.writeString(name)
        parcel.writeString(profilePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieCastModel> {
        override fun createFromParcel(parcel: Parcel): MovieCastModel {
            return MovieCastModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieCastModel?> {
            return arrayOfNulls(size)
        }
    }

}