package com.khmelenko.lab.varis.log

import org.junit.Assert
import org.junit.Test

import java.util.Arrays

class AnsiParserTest {

    @Test
    fun parseTextEmpty() {
        assertEquals(AnsiParser.parseText(""), TextLeaf())
    }

    @Test
    fun parseTextSimpleText() {
        assertEquals(AnsiParser.parseText("Hello World"), TextLeaf("Hello World"))
    }

    @Test
    fun parseTextSimpleTextMultiline() {
        assertEquals(AnsiParser.parseText("Hello World\nHello Mars"), TextLeaf("Hello World\nHello Mars"))
    }

    @Test
    fun parseTextHeader() {
        val textLeaf = TextLeaf("Worker information")
        textLeaf.options.isBold = true
        textLeaf.options.textColor = "#FFFFB6"
        assertEquals(AnsiParser.parseText("\u001B[33;1mWorker information\u001B[0m"), textLeaf)
    }

    @Test
    fun parseTextComplexHeader() {
        val headerLeaf = TextLeaf("Build system information")
        headerLeaf.options.textColor = "#96CBFE"
        assertEquals(AnsiParser.parseText("\u001B[0Ktravis_fold:start:system_info\n" +
                "\u001B[0K\u001B[34mBuild system information\u001B[0m\n" +
                "Build language: android\n"), TextLeaf("travis_fold:start:system_info"),
                headerLeaf, TextLeaf("\nBuild language: android\n"))
    }

    @Test
    fun parseTextResetEmptyLine() {
        assertEquals(AnsiParser.parseText("\u001B[0K"), TextLeaf())
    }

    @Test
    fun parseTextResetSingleLine() {
        assertEquals(AnsiParser.parseText("Hello World\u001B[0K"), TextLeaf())
    }

    @Test
    fun parseTextResetMultiLine() {
        assertEquals(AnsiParser.parseText("Hello World\nHello Mars\u001B[0KTest"), TextLeaf("Hello World"), TextLeaf("Test"))
    }

    @Test
    fun parseTextBackspace() {
        assertEquals(AnsiParser.parseText("Hello World\b\b\b\b\b\n\bMars"), TextLeaf("Hello Mars"))
    }

    private fun assertEquals(list: List<TextLeaf>, vararg leafs: TextLeaf) {
        Assert.assertEquals(Arrays.asList(*leafs).toString(), list.toString())
    }
}
