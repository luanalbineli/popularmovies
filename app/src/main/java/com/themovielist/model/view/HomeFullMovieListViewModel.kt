package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.util.readBoolean
import com.themovielist.util.writeBoolean

data class HomeFullMovieListViewModel(
        var filter: Int,
        var pageIndex: Int,
        var movieList: MutableList<MovieImageGenreViewModel> = mutableListOf(),
        var firstVisibleItemPosition: Int = 0,
        var imageResponseModel: ConfigurationImageResponseModel? = null,
        var hasMorePages: Boolean = false,
        var genreListItemModelList: List<GenreListItemModel> = ArrayList()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.createTypedArrayList(MovieImageGenreViewModel).toMutableList(),
            parcel.readInt(),
            parcel.readParcelable(ConfigurationImageResponseModel::class.java.classLoader),
            parcel.readBoolean(),
            parcel.createTypedArrayList(GenreListItemModel))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(filter)
        parcel.writeInt(pageIndex)
        parcel.writeTypedList(movieList)
        parcel.writeInt(firstVisibleItemPosition)
        parcel.writeParcelable(imageResponseModel, 0)
        parcel.writeBoolean(hasMorePages)
        parcel.writeTypedList(genreListItemModelList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<HomeFullMovieListViewModel> {
        override fun createFromParcel(parcel: Parcel): HomeFullMovieListViewModel =
                HomeFullMovieListViewModel(parcel)

        override fun newArray(size: Int): Array<HomeFullMovieListViewModel?> = arrayOfNulls(size)
    }
}