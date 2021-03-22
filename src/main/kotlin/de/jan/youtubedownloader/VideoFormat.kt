package de.jan.youtubedownloader

import org.json.JSONObject
import java.net.URL

class VideoFormat(private val data: JSONObject) {

    val extension: String = data.getString("ext")
    val id: String = data.getString("format_id")
    val url: URL = URL(data.getString("url"))
    val description: String = data.getString("format")
    val format_note: String = data.getString("format_note")
    val vcodec: String = data.getString("vcodec")
    val acodec: String = data.getString("acodec")
    val fileSize: VideoSize? = if(data.isNull("filesize")) null else VideoSize(data.getLong("filesize"))

    override fun toString(): String {
        return "$format_note | $extension"
    }
}