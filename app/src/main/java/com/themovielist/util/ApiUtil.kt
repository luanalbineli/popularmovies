package com.themovielist.util

object ApiUtil {
    const val ORIGINAL_IMAGE_SIZE_NAME = "original"

    private val BASE_URL_POSTER = "http://image.tmdb.org/t/p/"
    private val POSTER_SIZE = intArrayOf(92, 154, 185, 342, 500, 780) // TODO: Hardcoded, we should call /configuration.

    fun buildPosterImageUrl(posterKey: String, posterWidth: String): String {
        return BASE_URL_POSTER + posterWidth + "/" + posterKey
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

    const val INITIAL_PAGE_SIZE = 1

}
