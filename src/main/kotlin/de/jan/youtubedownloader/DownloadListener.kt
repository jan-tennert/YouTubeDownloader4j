package de.jan.youtubedownloader


interface DownloadListener {

    fun onDownloading(progress: Float, eta: Long)

    fun onFinish()

}