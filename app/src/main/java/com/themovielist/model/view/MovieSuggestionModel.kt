package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.themovielist.model.MovieModel


data class MovieSuggestionModel constructor(val movieModel: MovieModel) : SearchSuggestion {
    override fun getBody(): String {
        return movieModel.title
    }

    constructor(parcel: Parcel) : this(parcel.readParcelable<MovieModel>(MovieModel::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(movieModel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieSuggestionModel> {
        override fun createFromParcel(parcel: Parcel): MovieSuggestionModel {
            return MovieSuggestionModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieSuggestionModel?> {
            return arrayOfNulls(size)
        }
    }

}