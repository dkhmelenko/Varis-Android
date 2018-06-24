package com.khmelenko.lab.varis

import com.khmelenko.lab.varis.converter.TimeConverter

import org.junit.Test

import org.junit.Assert.assertEquals

/**
 * Testing TimeConverter class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestTimeConverter {

    @Test
    fun testDurationToString() {

        val zeroDuration: Long = 0
        val onlySeconds: Long = 45
        val onlyMinutes = (5 * 60).toLong()
        val onlyHours = (3 * 60 * 60).toLong()
        val secondsAndMinutes = onlyMinutes + onlySeconds
        val hoursMinutesAndSeconds = onlyHours + secondsAndMinutes
        val hoursAndSeconds = onlyHours + onlySeconds
        val hoursAndMinutes = onlyHours + onlyMinutes

        val zeroDurationString = "0 sec"
        val onlySecondsString = "45 sec"
        val onlyMinutesString = "5 min"
        val onlyHoursString = "3 hours"
        val secondsAndMinutesString = String.format("%s %s", onlyMinutesString, onlySecondsString)
        val hoursMinutesAndSecondsString = String.format("%s %s %s", onlyHoursString, onlyMinutesString, onlySecondsString)
        val hoursAndSecondsString = String.format("%s %s", onlyHoursString, onlySecondsString)
        val hoursAndMinutesString = String.format("%s %s", onlyHoursString, onlyMinutesString)

        var actual = TimeConverter.durationToString(onlySeconds)
        assertEquals(onlySecondsString, actual)

        actual = TimeConverter.durationToString(onlyMinutes)
        assertEquals(onlyMinutesString, actual)

        actual = TimeConverter.durationToString(onlyHours)
        assertEquals(onlyHoursString, actual)

        actual = TimeConverter.durationToString(secondsAndMinutes)
        assertEquals(secondsAndMinutesString, actual)

        actual = TimeConverter.durationToString(hoursMinutesAndSeconds)
        assertEquals(hoursMinutesAndSecondsString, actual)

        actual = TimeConverter.durationToString(hoursAndSeconds)
        assertEquals(hoursAndSecondsString, actual)

        actual = TimeConverter.durationToString(hoursAndMinutes)
        assertEquals(hoursAndMinutesString, actual)

        actual = TimeConverter.durationToString(zeroDuration)
        assertEquals(zeroDurationString, actual)
    }
}
