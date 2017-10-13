package com.albineli.udacity.popularmovies.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

data class MovieReviewModel constructor(@SerializedName("author") val author: String,
                                        @SerializedName("content") val content: String) : Parcelable {
    private constructor(parcel: Parcel): this(parcel.readString(), parcel.readString())

    private constructor(): this("", "")

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(author)
        dest.writeString(content)
    }

    companion object {

        val CREATOR: Parcelable.Creator<MovieReviewModel> = object : Parcelable.Creator<MovieReviewModel> {
            override fun createFromParcel(`in`: Parcel): MovieReviewModel {
                return MovieReviewModel(`in`)
            }

            override fun newArray(size: Int): Array<MovieReviewModel> {
                return Array(size) { MovieReviewModel() }
            }
        }
    }
}
