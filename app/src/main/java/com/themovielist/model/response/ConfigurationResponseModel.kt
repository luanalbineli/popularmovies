package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.enums.ImageSizeEnum
import com.themovielist.model.MovieSizeModel
import com.themovielist.util.ApiUtil.ORIGINAL_IMAGE_SIZE_NAME
import timber.log.Timber

data class ConfigurationResponseModel constructor(@SerializedName("images") val imageResponseModel: ConfigurationImageResponseModel)

data class ConfigurationImageResponseModel constructor(@SerializedName("secure_base_url") val secureBaseUrl: String,
                                                       @SerializedName("profile_sizes") private val profileSizes: List<String>) {


    private var mProfileSizeList: List<MovieSizeModel>? = null

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

    /* TODO: For some unknown reason, thje profileSizeList is null :(
       TODO: I already tried to put on the constructor, as @Transient and init on the default cconstructor, as lateinit var */


    /*@delegate:Transient
    val profileSizeList by lazy {
        profileSizes.mapNotNull {
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
}
