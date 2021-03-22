package de.jan.youtubedownloader

import org.json.JSONObject
import java.util.*

class TwitterVideoInfo(private val data: JSONObject) {

    val comments = data.getLong("comment_count")
    val uploader = data.getString("uploader")
    val title = data.getString("fulltitle")
    val description = data.getString("description")
    val thumbnail = data.getString("thumbnail")
    val like_count = data.getLong("like_count")
    val url = data.getString("webpage_url")
    val uploader_url = data.getString("uploader_url")
    val tags = arrayOf(data.getJSONArray("tags"))
    val video_length = VideoLength(data.getDouble("duration").toLong())
    val upload_date = Date(data.getLong("timestamp") * 1000)
    val filename = data.getString("_filename")

}