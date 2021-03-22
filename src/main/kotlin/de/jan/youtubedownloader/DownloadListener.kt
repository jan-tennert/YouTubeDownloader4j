package de.jan.youtubedownloader

import java.io.File

interface DownloadListener {

    fun onDownloading(progress: Float, eta: Long)

    fun onFinish()

}