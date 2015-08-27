package com.khmelenko.lab.travisclient.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides utils methods for processing data & time
 *
 * @author Dmytro Khmelenko
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    /**
     * Formats date time structure to the local string
     *
     * @param dateTime Datetime
     * @return Formatted date time in local
     */
    public static String formatDateTimeLocal(Date dateTime) {
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
        String formatted = formatter.format(dateTime);
        return formatted;
    }

}
