package com.khmelenko.lab.varis.converter

/**
 * Provides time related convertion methods
 *
 * @author Dmytro Khmelenko
 */
object TimeConverter {

    /**
     * Converts duration from seconds to formatted string
     *
     * @param duration Duration in seconds
     * @return Formatted string
     */
    @JvmStatic
    fun durationToString(duration: Long): String {
        val hour = (duration / 3600).toInt()
        val min = ((duration - hour * 3600) / 60).toInt()
        val sec = (duration % 60).toInt()
        val builder = StringBuilder()
        if (hour != 0) {
            builder.append(hour).append(" hours")
        }
        if (min != 0) {
            if (builder.isNotEmpty()) {
                builder.append(" ")
            }
            builder.append(min).append(" min")
        }
        if (sec != 0) {
            if (builder.isNotEmpty()) {
                builder.append(" ")
            }
            builder.append(sec).append(" sec")
        }

        if (duration == 0L) {
            builder.append(sec).append(" sec")
        }

        return builder.toString()
    }
}
