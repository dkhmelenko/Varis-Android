package com.khmelenko.lab.travisclient.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static Date parseXmlDateTime(String xmlDateTime) throws ParseException {
        xmlDateTime = xmlDateTime.replace("Z", "+00:00");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        Date parsedDate = dateFormat.parse(xmlDateTime);
        return parsedDate;
    }

}
