package com.themovielist.model.view

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import com.themovielist.model.GenreModel
import com.themovielist.model.response.ConfigurationImageResponseModel
import com.themovielist.util.readBoolean
import com.themovielist.util.values
import com.themovielist.util.writeBoolean

data class HomeFullMovieListViewModel(
        var filter: Int,
        var pageIndex: Int,
        var movieList: MutableList<MovieImageGenreViewModel> = mutableListOf(),
        var firstVisibleItemPosition: Int = 0,
        var imageResponseModel: ConfigurationImageResponseModel? = null,
        var hasMorePages: Boolean = false,
        var genreMap: SparseArray<GenreModel> = SparseArray(),
        var selectedGenreMap: SparseArray<GenreModel> = SparseArray()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.createTypedArrayList(MovieImageGenreViewModel).toMutableList(),
            parcel.readInt(),
            parcel.readParcelable(ConfigurationImageResponseModel::class.java.classLoader),
            parcel.readBoolean()) {
        parcel.createTypedArrayList(GenreModel).map {
            genreMap.append(it.id, it)
        }

        parcel.createTypedArrayList(GenreModel).map {
            selectedGenreMap.append(it.id, it)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(filter)
        parcel.writeInt(pageIndex)
        parcel.writeTypedList(movieList)
        parcel.writeInt(firstVisibleItemPosition)
        parcel.writeParcelable(imageResponseModel, 0)
        parcel.writeBoolean(hasMorePages)
        parcel.writeTypedList(genreMap.values())
        parcel.writeTypedList(selectedGenreMap.values())
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<HomeFullMovieListViewModel> {
        override fun createFromParcel(parcel: Parcel): HomeFullMovieListViewModel =
                HomeFullMovieListViewModel(parcel)

        override fun newArray(size: Int): Array<HomeFullMovieListViewModel?> = arrayOfNulls(size)
    }
}