package de.jan.youtubedownloader

import org.json.JSONObject
import java.io.File

import java.util.*


class YouTubeDownloader {

    private var youtubeDL: String = "youtube-dl"

    fun downloadVideo(video: Builder) {
        val proc = getYTDLBuilder(false, *video.build().split(" ").toTypedArray())

        video.directory.also {
            proc.directory(video.directory)
        }
        proc.start()
    }

    fun downloadVideo(video: Builder, listener: DownloadListener) {
        val proc = getYTDLBuilder(false, *video.build().split(" ").toTypedArray())

        video.directory?.let {
            proc.directory(video.directory)
        }
        val process = proc.start()

        val outBuffer = StringBuffer()

        ProgressExtractor(outBuffer, process.inputStream, listener)

        process.waitFor()

        listener.onFinish()
    }

    fun getFormats(url: String): Array<VideoFormat> {
        val formats = arrayListOf<VideoFormat>()
        val json = JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url)))
        println(json)
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

    fun getFilename(url: String) : String {
        return JSONObject(execCmd(getYTDLBuilder(false, "--dump-json", url))).getString("_filename")
    }

    private fun execCmd(proc: ProcessBuilder): String {
        val s = Scanner(proc.start().inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
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

    class Builder(val url: String) {

        var directory: File? = null
            private set
        var fileName: String? = null
            private set
        private var format: String? = null

        fun directory(directory: File) : Builder {
            this.directory = directory
            return this
        }

        fun fileName(name: String) : Builder {
            fileName = name
            return this
        }

        fun format(format: VideoFormat) : Builder {
            this.format = format.id.toString()
            return this
        }

        fun highestQuality() : Builder {
            this.format = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4"
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

        fun build() : String {
            var options = ""

            fileName?.let {
                options += " -o $fileName "
            }

            format?.let {
                options += " -f ${format!!} "
            }

            options = options.replace("  ", " ")
            if(options.startsWith(" ")) {
                options = options.substring(1)
            }

            options += url
            return options
        }

    }

}

class YouTubeDownloaderException(e: String) : Exception(e)
