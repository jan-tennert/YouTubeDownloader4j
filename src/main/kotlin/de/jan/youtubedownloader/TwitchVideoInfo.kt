package de.jan.youtubedownloader

import org.json.JSONObject
import java.util.*

class TwitchVideoInfo(private val data: JSONObject) {

    val uploader = data.getString("uploader")
    val title = data.getString("fulltitle")
    val thumbnail = data.getString("thumbnail")
    val url = data.getString("webpage_url")
    val view_count = data.getLong("view_count")
    val uploader_url = "https://twitch.tv/${data.getString("uploader_id")}"
    val upload_date = Date(data.getLong("timestamp") * 1000)
    val video_length = VideoLength(data.getLong("duration"))
    val filename = data.getString("_filename")

}