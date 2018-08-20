package com.khmelenko.lab.varis.log

import org.junit.Test

import org.junit.Assert.assertEquals

class TextLeafTest {
    @Test
    fun addAnsiCodes() {
        val actual = TextLeaf("Magenta Italic", FormattingOptions.fromAnsiCodes(*arrayOf("35", "3")))
        val expected = TextLeaf("Magenta Italic")
        expected.options.textColor = "#FF73FD"
        expected.options.isItalic = true
        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun toHtml() {
        val text = TextLeaf("T", FormattingOptions.fromAnsiCodes(*arrayOf("34", "1")))
        assertEquals("<b><font color='#96CBFE'>T</font></b>", text.toHtml())
    }
}
