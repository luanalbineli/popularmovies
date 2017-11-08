package com.themovielist.model.response

import com.google.gson.annotations.SerializedName
import com.themovielist.enums.ImageSizeEnum
import com.themovielist.model.MovieSizeModel
import timber.log.Timber

data class ConfigurationResponseModel constructor(@SerializedName("images") val imageResponseModel: ConfigurationImageResponseModel)

data class ConfigurationImageResponseModel constructor(@SerializedName("secure_base_url") val secureBaseUrl: String,
                                                       @SerializedName("profile_sizes") private val profileSizes: List<String>) {
    @Transient
    val movieSizeList: List<MovieSizeModel>

    init {
        movieSizeList = profileSizes.mapNotNull {
            try {
                val size = it.substring(1).toInt()
                val sizeType = if (it.startsWith('h', ignoreCase = true)) ImageSizeEnum.HEIGHT else ImageSizeEnum.WIDTH
                MovieSizeModel(size, sizeType)
            } catch (ex: Exception) {
                Timber.e(ex, "An error occurred while tried to parse the size: ${ex.message}")
                null
            }
        }
    }
}
