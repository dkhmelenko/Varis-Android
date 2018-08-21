package com.khmelenko.lab.varis.log

import java.util.Stack

/**
 * Parser for ANSI escape sequences.
 *
 * @see [Wikipedia](https://en.wikipedia.org/wiki/ANSI_escape_code)
 */
internal class AnsiParser {

    private val result = Stack<TextLeaf>()
    private var options = FormattingOptions.fromAnsiCodes()

    /**
     * @see .parseText
     */
    private fun parse(inputText: String): Stack<TextLeaf> {
        var text = inputText
        // Remove character when followed by a BACKSPACE character
        while (text.contains("\b")) {
            text = text.replace("^\b+|[^\b]\b".toRegex(), "")
        }

        var controlStartPosition: Int
        var lastControlPosition = 0
        var controlEndPosition: Int
        while (true) {
            controlStartPosition = text.indexOf("\u001b[", lastControlPosition)
            if (controlStartPosition == -1) {
                break
            }

            val textBeforeEscape = text.substring(lastControlPosition, controlStartPosition)
            emitText(textBeforeEscape)

            if (isResetLineEscape(text, controlStartPosition)) {
                controlEndPosition = text.indexOf('K', controlStartPosition + 2)
                removeCurrentLine()
                options = FormattingOptions.fromAnsiCodes()
            } else {
                controlEndPosition = text.indexOf('m', controlStartPosition + 2)
                if (controlEndPosition == -1) {
                    break
                }
                val matchingData = text.substring(controlStartPosition + 2, controlEndPosition)
                val ansiStates = matchingData.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                options = FormattingOptions.fromAnsiCodes(*ansiStates)
            }

            lastControlPosition = controlEndPosition + 1
        }

        emitText(text.substring(lastControlPosition))
        if (result.isEmpty()) {
            result.push(TextLeaf())
        }
        return result
    }

    private fun emitText(text: String) {
        if (!text.isEmpty()) {
            result.push(TextLeaf(text, options))
        }
    }

    private fun removeCurrentLine() {
        if (result.isEmpty()) {
            return
        }
        var textLeaf = result.peek()
        var i: Int
        while (true) {
            i = Math.max(textLeaf.text.lastIndexOf("\r"), textLeaf.text.lastIndexOf("\n"))
            if (i != -1) {
                break
            }
            result.pop()
            if (result.isEmpty()) {
                break
            }
            textLeaf = result.peek()
        }
        if (textLeaf.text.length > i && i > 0) {
            val end = textLeaf.text.substring(i - 1, i + 1)
            textLeaf.text = textLeaf.text.substring(0, i)
            if (end == "\r\n" || end == "\n\r") {
                textLeaf.text = textLeaf.text.substring(0, i - 1)
            }
        } else {
            textLeaf.text = ""
        }
    }

    private fun isResetLineEscape(str: String, controlStartPosition: Int): Boolean {
        val substring = str.substring(controlStartPosition)
        return substring.startsWith("\u001b[0K") || substring.startsWith("\u001b[K")
    }

    companion object {

        /**
         * Parses the given log for ANSI escape sequences and builds a list of text chunks, which
         * share the same color and text formatting.
         */
        fun parseText(text: String): Stack<TextLeaf> {
            return AnsiParser().parse(text)
        }
    }
}
