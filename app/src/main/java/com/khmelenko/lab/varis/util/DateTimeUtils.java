package com.khmelenko.lab.varis.util;

import java.text.DateFormat;
import java.text.ParseException;
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

    /**
     * Parses XML datetime object to Date object
     *
     * @param xmlDateTime XML Datetime as a String
     * @return Datetime object
     * @throws ParseException if Parsing exception occurred
     */
    public static Date parseXmlDateTime(String xmlDateTime) {
        Date parsedDate = new Date();
        try {
            xmlDateTime = xmlDateTime.replace("Z", "+0000");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            parsedDate = dateFormat.parse(xmlDateTime);
            return parsedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    /**
     * Parses and formats xml date time string
     *
     * @param xmlDateTime XML date time
     * @return Formatted date time
     */
    public static String parseAndFormatDateTime(String xmlDateTime) {
        Date date = DateTimeUtils.parseXmlDateTime(xmlDateTime);
        String formattedDate = DateTimeUtils.formatDateTimeLocal(date);
        return formattedDate;
    }

}
