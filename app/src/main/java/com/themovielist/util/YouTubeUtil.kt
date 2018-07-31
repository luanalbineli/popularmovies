package com.themovielist.util


import android.content.Context
import android.content.Intent
import android.net.Uri

object YouTubeUtil {
    private const val VIDEO_URL_OPEN = "https://www.youtube.com/watch?v=%1\$s"

    // TODO: Try to open the YouTube app first.
    fun openYouTubeVideo(context: Context, videoKey: String) {
        val uri = Uri.parse(String.format(VIDEO_URL_OPEN, videoKey))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}
