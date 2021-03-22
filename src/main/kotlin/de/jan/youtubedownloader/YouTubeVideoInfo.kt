package de.jan.youtubedownloader

import org.json.JSONObject
import java.text.SimpleDateFormat

class YouTubeVideoInfo(private val data: JSONObject) {

    private val format = SimpleDateFormat("yyyyMMdd")
    val uploader_url = data.getString("channel_url")
    val view_count = data.getLong("view_count")
    val dislike_count = data.getLong("dislike_count")
    val like_count = data.getLong("like_count")
    val thumbnail = data.getString("thumbnail")
    val title = data.getString("fulltitle")
    val tags = arrayOf(data.getJSONArray("tags"))
    val uploader = data.getString("uploader")
    val video_url = data.getString("webpage_url")
    val upload_date = format.parse(data.getString("upload_date"))
    val video_length = VideoLength(data.getLong("duration"))
    val description = data.getString("description")
    val filename = data.getString("_filename")


}