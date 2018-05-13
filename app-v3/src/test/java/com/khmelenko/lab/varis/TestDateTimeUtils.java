package com.khmelenko.lab.varis;

import com.khmelenko.lab.varis.util.DateTimeUtils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

/**
 * Testing DateTimeUtils class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class TestDateTimeUtils {

    @Test
    public void testParseXmlDateTime() throws ParseException {
        Calendar expected = new GregorianCalendar();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String xmlDate = formatter.format(expected.getTime());
        Date actual = DateTimeUtils.parseXmlDateTime(xmlDate);

        assertEquals(expected.getTime().getTime() / 1000, actual.getTime() / 1000);
    }

}
