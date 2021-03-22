package de.jan.youtubedownloader

class VideoSize(val bytes: Long) {

    fun toKilobyte() : Double {
        return bytes / 1000.toDouble()
    }

    fun toMegabyte() : Double {
        return bytes / 1000000.toDouble()
    }

    fun toGigabyte() : Double {
        return bytes / 1000000000.toDouble()
    }

    override fun toString(): String {
        return toMegabyte().toString()
    }

}