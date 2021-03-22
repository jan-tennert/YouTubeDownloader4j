package de.jan.youtubedownloader

import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder
import java.util.regex.Matcher
import java.util.regex.Pattern


class ProgressExtractor(buffer: StringBuffer?, stream: InputStream?, callback: DownloadListener?) : Thread() {
    private val GROUP_PERCENT = "percent"
    private val GROUP_MINUTES = "minutes"
    private val GROUP_SECONDS = "seconds"
    private var stream: InputStream? = null
    private var buffer: StringBuffer? = null
    private var callback: DownloadListener? = null

    private val p: Pattern =
        Pattern.compile("\\[download\\]\\s+(?<percent>\\d+\\.\\d)% .* ETA (?<minutes>\\d+):(?<seconds>\\d+)")

    init {
        this.stream = stream
        this.buffer = buffer
        this.callback = callback
        this.start()
    }

    override fun run() {
        try {
            val currentLine = StringBuilder()
            var nextChar: Int
            while (stream!!.read().also { nextChar = it } != -1) {
                buffer!!.append(nextChar.toChar())
                if (nextChar == '\r'.toInt() && callback != null) {
                    processOutputLine(currentLine.toString())
                    currentLine.setLength(0)
                    continue
                }
                currentLine.append(nextChar.toChar())
            }
        } catch (ignored: IOException) {
        }
    }

    private fun processOutputLine(line: String) {
        val m: Matcher = p.matcher(line)
        if (m.matches()) {
            val progress: Float = m.group(GROUP_PERCENT).toFloat()
            val eta = convertToSeconds(m.group(GROUP_MINUTES), m.group(GROUP_SECONDS)).toLong()
            callback!!.onDownloading(progress, eta)
        }
    }

    private fun convertToSeconds(minutes: String, seconds: String): Int {
        return minutes.toInt() * 60 + seconds.toInt()
    }
}