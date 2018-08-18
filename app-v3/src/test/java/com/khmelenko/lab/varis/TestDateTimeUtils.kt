package com.khmelenko.lab.varis

import com.khmelenko.lab.varis.util.DateTimeUtils
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

/**
 * Testing DateTimeUtils class
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class TestDateTimeUtils {

    @Test
    fun testParseXmlDateTime() {
        val expected = GregorianCalendar()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val xmlDate = formatter.format(expected.time)
        val actual = DateTimeUtils.parseXmlDateTime(xmlDate)

        assertEquals(expected.time.time / 1000, actual.time / 1000)
    }
}
