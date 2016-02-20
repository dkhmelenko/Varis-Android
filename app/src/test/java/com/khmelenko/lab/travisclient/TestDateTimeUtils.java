package com.khmelenko.lab.travisclient;

import com.ibm.icu.impl.duration.DateFormatter;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

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
