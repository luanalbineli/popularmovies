package com.themovielist.model.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.themovielist.enums.ImageSizeEnum
import com.themovielist.model.MovieSizeModel
import com.themovielist.util.ApiUtil.ORIGINAL_IMAGE_SIZE_NAME
import timber.log.Timber

data class ConfigurationResponseModel constructor(@SerializedName("images") val imageResponseModel: ConfigurationImageResponseModel)

data class ConfigurationImageResponseModel constructor(@SerializedName("secure_base_url") val secureBaseUrl: String,
                                                       @SerializedName("poster_sizes") private val posterSizes: List<String>,
                                                       @SerializedName("profile_sizes") private val profileSizes: List<String>) : Parcelable {


    private var mPosterSizeList: List<MovieSizeModel>? = null
    private var mProfileSizeList: List<MovieSizeModel>? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.createStringArrayList()) {
        mPosterSizeList = parcel.createTypedArrayList(MovieSizeModel)
    }

    fun getPosterSizeList(): List<MovieSizeModel> {
        if (mPosterSizeList == null) {
            mPosterSizeList = posterSizes.mapNotNull {
                try {
                    if (it.equals(ORIGINAL_IMAGE_SIZE_NAME, ignoreCase = true)) { // Ignores the original size type
                        null
                    } else {
                        val size = it.substring(1).toInt()
                        val sizeType = if (it.startsWith('h', ignoreCase = true)) ImageSizeEnum.HEIGHT else ImageSizeEnum.WIDTH
                        MovieSizeModel(size, sizeType)
                    }
                } catch (ex: Exception) {
                    Timber.e(ex, "An error occurred while tried to parse the size: ${ex.message}")
                    null
                }
            }
        }
        return mPosterSizeList!!
    }

    fun getProfileSizeList(): List<MovieSizeModel> {
        if (mProfileSizeList == null) {
            mProfileSizeList = profileSizes.mapNotNull {
                try {
                    if (it.equals(ORIGINAL_IMAGE_SIZE_NAME, ignoreCase = true)) { // Ignores the original size type
                        null
                    } else {
                        val size = it.substring(1).toInt()
                        val sizeType = if (it.startsWith('h', ignoreCase = true)) ImageSizeEnum.HEIGHT else ImageSizeEnum.WIDTH
                        MovieSizeModel(size, sizeType)
                    }
                } catch (ex: Exception) {
                    Timber.e(ex, "An error occurred while tried to parse the size: ${ex.message}")
                    null
                }
            }
        }
        return mProfileSizeList!!
    }

    /* TODO: For some unknown reason, the profileSizeList is null :(
       TODO: I already tried to put on the constructor, as @Transient and init on the default constructor, as lateinit var */

    /*@delegate:Transient
    val profileSizeList by lazy {
        posterSizes.mapNotNull {
            try {
                val size = it.substring(1).toInt()
                val sizeType = if (it.startsWith('h', ignoreCase = true)) ImageSizeEnum.HEIGHT else ImageSizeEnum.WIDTH
                MovieSizeModel(size, sizeType)
            } catch (ex: Exception) {
                Timber.e(ex, "An error occurred while tried to parse the size: ${ex.message}")
                null
            }
        }
    }*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(secureBaseUrl)
        parcel.writeStringList(posterSizes)
        parcel.writeTypedList(mPosterSizeList)
        parcel.writeTypedList(mProfileSizeList)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ConfigurationImageResponseModel> {
        override fun createFromParcel(parcel: Parcel): ConfigurationImageResponseModel =
                ConfigurationImageResponseModel(parcel)

        override fun newArray(size: Int): Array<ConfigurationImageResponseModel?> =
                arrayOfNulls(size)
    }
}
