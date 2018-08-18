package com.khmelenko.lab.varis.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Provides utils methods for processing data & time
 *
 * @author Dmytro Khmelenko
 */
object DateTimeUtils {

    /**
     * Formats date time structure to the local string
     *
     * @param dateTime Datetime
     * @return Formatted date time in local
     */
    fun formatDateTimeLocal(dateTime: Date): String {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        return formatter.format(dateTime)
    }

    /**
     * Parses XML datetime object to Date object
     *
     * @param xmlDateTime XML Datetime as a String
     * @return Datetime object
     * @throws ParseException if Parsing exception occurred
     */
    fun parseXmlDateTime(xmlDateTime: String): Date {
        var xmlDateTime = xmlDateTime
        var parsedDate = Date()
        try {
            xmlDateTime = xmlDateTime.replace("Z", "+0000")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            parsedDate = dateFormat.parse(xmlDateTime)
            return parsedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return parsedDate
    }

    /**
     * Parses and formats xml date time string
     *
     * @param xmlDateTime XML date time
     * @return Formatted date time
     */
    fun parseAndFormatDateTime(xmlDateTime: String): String {
        val date = DateTimeUtils.parseXmlDateTime(xmlDateTime)
        return formatDateTimeLocal(date)
    }
}
