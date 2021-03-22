package de.jan.youtubedownloader

import java.time.Duration
import kotlin.math.abs

class VideoLength(val seconds: Long) {

    fun toMinutes() : Double {
        return seconds / 60.toDouble()
    }

    fun toHours() : Double {
        return seconds / 3600.toDouble()
    }

    fun toDuration() : String {
        val dur = Duration.ofMillis(seconds * 1000)
        return formatDuration(dur)
    }

    private fun formatDuration(duration: Duration): String {
        val seconds = duration.seconds
        val absSeconds = abs(seconds)
        val positive = String.format(
            "%d:%02d:%02d",
            absSeconds / 3600,
            absSeconds % 3600 / 60,
            absSeconds % 60
        )
        return if (seconds < 0) "-$positive" else positive
    }

    override fun toString(): String {
        return toDuration()
    }

}