package com.khmelenko.lab.travisclient;

import com.khmelenko.lab.travisclient.converter.TimeConverter;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing TimeConverter class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestTimeConverter {

    @Test
    public void testDurationToString() {

        long zeroDuration = 0;
        long onlySeconds = 45;
        long onlyMinutes = 5 * 60;
        long onlyHours = 3 * 60 * 60;
        long secondsAndMinutes = onlyMinutes + onlySeconds;
        long hoursMinutesAndSeconds = onlyHours + secondsAndMinutes;
        long hoursAndSeconds = onlyHours + onlySeconds;
        long hoursAndMinutes = onlyHours + onlyMinutes;

        String zeroDurationString = "0 sec";
        String onlySecondsString = "45 sec";
        String onlyMinutesString = "5 min";
        String onlyHoursString = "3 hours";
        String secondsAndMinutesString = String.format("%s %s", onlyMinutesString, onlySecondsString);
        String hoursMinutesAndSecondsString = String.format("%s %s %s", onlyHoursString, onlyMinutesString, onlySecondsString);
        String hoursAndSecondsString = String.format("%s %s", onlyHoursString, onlySecondsString);
        String hoursAndMinutesString = String.format("%s %s", onlyHoursString, onlyMinutesString);

        String actual = TimeConverter.durationToString(onlySeconds);
        assertEquals(onlySecondsString, actual);

        actual = TimeConverter.durationToString(onlyMinutes);
        assertEquals(onlyMinutesString, actual);

        actual = TimeConverter.durationToString(onlyHours);
        assertEquals(onlyHoursString, actual);

        actual = TimeConverter.durationToString(secondsAndMinutes);
        assertEquals(secondsAndMinutesString, actual);

        actual = TimeConverter.durationToString(hoursMinutesAndSeconds);
        assertEquals(hoursMinutesAndSecondsString, actual);

        actual = TimeConverter.durationToString(hoursAndSeconds);
        assertEquals(hoursAndSecondsString, actual);

        actual = TimeConverter.durationToString(hoursAndMinutes);
        assertEquals(hoursAndMinutesString, actual);

        actual = TimeConverter.durationToString(zeroDuration);
        assertEquals(zeroDurationString, actual);
    }
}
