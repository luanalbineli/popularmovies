package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.themovielist.model.MovieModel


data class SearchSuggestionModel constructor(val movieModel: MovieModel) : SearchSuggestion {
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

    companion object CREATOR : Parcelable.Creator<SearchSuggestionModel> {
        override fun createFromParcel(parcel: Parcel): SearchSuggestionModel {
            return SearchSuggestionModel(parcel)
        }

        override fun newArray(size: Int): Array<SearchSuggestionModel?> {
            return arrayOfNulls(size)
        }
    }

}