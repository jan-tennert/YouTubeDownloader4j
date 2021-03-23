package de.jan.youtubedownloader

import org.json.JSONObject
import java.io.File

import java.util.*


class YouTubeDownloader {

    private var youtubeDL: String = "youtube-dl"
    private var ffmpeg: String = "ffmpeg"

    fun downloadVideo(video: Builder) : File? {
        val download = video.build()

        val proc = getYTDLBuilder(true, *download.command.split(" ").toTypedArray())

        val process = proc.start()

        process.waitFor()

        return download.file
    }


    fun downloadVideo(video: Builder, listener: DownloadListener) {
        val proc = getYTDLBuilder(false, *video.build().command.split(" ").toTypedArray())

        val process = proc.start()

        val outBuffer = StringBuffer()

        ProgressExtractor(outBuffer, process.inputStream, listener)

        process.waitFor()

        listener.onFinish(video.build().file)
    }

    fun downloadPlaylist(url: String, output: File, audioOnly: Boolean) {
        val args = arrayListOf<String>()
        if(audioOnly) {
            args.addAll(listOf("--extract-audio", "--audio-format", "mp3", "-o", "%(title)s.%(ext)s"))
        }
        args.add(url)
        val proc = getYTDLBuilder(true, *args.toTypedArray())
            .directory(output)
        proc.start()
            .waitFor()
    }

    fun getFormats(url: String): Array<VideoFormat> {
        val formats = arrayListOf<VideoFormat>()
        val json = JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url)))
        for (any in json.getJSONArray("formats")) {
            val format = any as JSONObject
            formats.add(VideoFormat(format))
        }
        return formats.toTypedArray()
    }

    fun getFormats(url: String, removeDuplicates: Boolean = false, filter: (VideoFormat) -> Boolean) : Array<VideoFormat> {
        val formats = getFormats(url).filter(filter).toTypedArray().distinctBy { f -> f.format_note }
        return formats.toTypedArray()
    }

    fun getFormat(url: String, resolution: String, extension: String?) : VideoFormat? {
        var formats = getFormats(url, true) { f -> f.format_note == resolution }
        extension?.let {
            formats = formats.filter { f -> f.extension == extension}.toTypedArray()
        }
        if(formats.isNotEmpty()) {
            return formats[0]
        }
        return null
    }

    fun getYouTubeVideoInfo(url: String) : YouTubeVideoInfo {
        return YouTubeVideoInfo(JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url))))
    }

    fun getTwitterVideoInfo(url: String) : TwitterVideoInfo {
        return TwitterVideoInfo(JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url))))
    }

    fun getTwitchVideoInfo(url: String) : TwitchVideoInfo {
        return TwitchVideoInfo(JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url))))
    }

    fun setCustomYTDLPath(path: String) {
        validatePath(path)
        youtubeDL = path
    }

    fun setCustomFFMPEGPath(path: String) {
        validatePath(path)
        ffmpeg = path
    }

    private fun execCmd(proc: ProcessBuilder): String {
        val s = Scanner(proc.start().inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    fun mergeVideoAndAudio(audio: String, video: String, output: String) : File {
        ProcessBuilder()
            .command("ffmpeg", "-i", video, "-i", audio, "-c:v", "copy", "-c:a", "aac", output)
            .start().waitFor()
        return File(output)
    }

    private fun getYTDLBuilder(output: Boolean, vararg args: String) : ProcessBuilder {
        val proc = ProcessBuilder()
            .command(youtubeDL, *args)
        if(output) {
            proc.inheritIO()
        }
        return proc
    }

    private fun validatePath(path: String) {
        if(!File(path).exists()) {
            throw YouTubeDownloaderException("The youtube-dl executable doesn't exist")
        }
    }

    class Builder(private val url: String) {

        var output: File? = null
        private var format: String? = null
        private var downloadRate: String? = null

        fun output(path: String) : Builder {
            output = File(path)
            return this
        }

        fun format(format: VideoFormat) : Builder {
            this.format = format.id
            return this
        }

        fun highestQuality() : Builder {
            this.format = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4"
            return this
        }

        /**
         * @param rate Can be like 20K = 20 Kilobyte or 20M = 20 Megabyte
         */
        fun downloadRate(rate: String) : Builder {
            downloadRate = rate
            return this
        }

        /**
         * If you want to add the thumbnail and metadata to the audio file you have to install ffmpeg and atomicparsley
         */
        fun onlyAudio(thumbnail: Boolean = false, metadata: Boolean = false) : Builder {
            this.format = "bestaudio[ext=m4a]"

            if(thumbnail) {
                format += " --embed-thumbnail"
            }
            if(metadata) {
                format += " --add-metadata $url"
            }
            return this
        }

        fun build() : YouTubeDownload {
            var options = ""

            output?.let {
                options += " -o ${output!!.absolutePath}"
            }

            format?.let {
                options += " -f ${format!!} "
            }

            downloadRate?.let {
                options += " -r ${downloadRate!!} "
            }

            options = options.replace("  ", " ")
            if(options.startsWith(" ")) {
                options = options.substring(1)
            }

            options += url
            return YouTubeDownload(options, output!!)
        }

    }

}

class YouTubeDownloaderException(e: String) : Exception(e)