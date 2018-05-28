package com.themovielist.util

import com.themovielist.enums.ImageSizeEnum
import com.themovielist.model.MovieSizeModel
import com.themovielist.model.response.ConfigurationImageResponseModel

object ApiUtil {
    const val ORIGINAL_IMAGE_SIZE_NAME = "original"

    private const val BASE_URL_POSTER = "http://image.tmdb.org/t/p/"
    private val POSTER_SIZE = intArrayOf(92, 154, 185, 342, 500, 780) // TODO: Hardcoded, we should call /configuration.

    fun buildPosterImageUrl(posterKey: String, posterWidth: String): String {
        return "$BASE_URL_POSTER$posterWidth/$posterKey"
    }

    fun getDefaultPosterSize(widthPx: Int): String {
        if (widthPx > POSTER_SIZE[POSTER_SIZE.size - 1]) {
            return "original"
        }
        return POSTER_SIZE
                .firstOrNull { it > widthPx }
                ?.let { "w" + it }
                ?: "original"
    }

    fun buildPosterImageUrl(posterKey: String, configurationImageResponseModel: ConfigurationImageResponseModel, posterWidth: Int, posterHeight: Int): String {
        val posterSize = getPosterSize(configurationImageResponseModel.getPosterSizeList(), posterWidth, posterHeight)
        return "${configurationImageResponseModel.secureBaseUrl}$posterSize/$posterKey"
    }

    private fun getPosterSize(posterSizeList: List<MovieSizeModel>, posterWidth: Int, posterHeight: Int): String {
        val posterSize = posterSizeList.firstOrNull {
            if (it.sizeType == ImageSizeEnum.WIDTH) {
                it.size > posterWidth
            }

            it.size > posterHeight
        } ?: return ORIGINAL_IMAGE_SIZE_NAME

        if (posterSize.sizeType == ImageSizeEnum.WIDTH) {
            return "w${posterSize.size}"
        }
        return "h${posterSize.size}"
    }

    const val INITIAL_PAGE_INDEX = 1

}
