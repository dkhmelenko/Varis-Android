package com.khmelenko.lab.varis.log

internal object AnsiCodes {
    private const val BLACK = "#4E4E4E"
    private const val RED = "#FF6C60"
    private const val GREEN = "#98D86A"
    private const val YELLOW = "#FFFFB6"
    private const val BLUE = "#96CBFE"
    private const val MAGENTA = "#FF73FD"
    private const val CYAN = "#50FFFF"
    private const val WHITE = "#E0E0E0"
    private const val GREY = "#969696"

    private const val BG_BLACK = "#4E4E4E"
    private const val BG_RED = "#FF6C60"
    private const val BG_GREEN = "#98D86A"
    private const val BG_YELLOW = "#FFFFB6"
    private const val BG_BLUE = "#96CBFE"
    private const val BG_MAGENTA = "#FF73FD"
    private const val BG_CYAN = "#50FFFF"
    private const val BG_WHITE = "#EEEEEE"

    fun applyAnsiCode(options: FormattingOptions, code: String) {
        when (code) {
            "1" -> options.isBold = true
            "3" -> options.isItalic = true
            "4" -> options.isUnderline = true
            "22" -> options.isBold = false
            "23" -> options.isItalic = false
            "24" -> options.isUnderline = false
            "30" -> options.textColor = AnsiCodes.BLACK
            "31" -> options.textColor = AnsiCodes.RED
            "32" -> options.textColor = AnsiCodes.GREEN
            "33" -> options.textColor = AnsiCodes.YELLOW
            "34" -> options.textColor = AnsiCodes.BLUE
            "35" -> options.textColor = AnsiCodes.MAGENTA
            "36" -> options.textColor = AnsiCodes.CYAN
            "37" -> options.textColor = AnsiCodes.WHITE
            "39" -> options.textColor = null
            "49" -> options.background = null
            "40" -> options.background = AnsiCodes.BG_BLACK
            "41" -> options.background = AnsiCodes.BG_RED
            "42" -> options.background = AnsiCodes.BG_GREEN
            "43" -> options.background = AnsiCodes.BG_YELLOW
            "44" -> options.background = AnsiCodes.BG_BLUE
            "45" -> options.background = AnsiCodes.BG_MAGENTA
            "46" -> options.background = AnsiCodes.BG_CYAN
            "47" -> options.background = AnsiCodes.BG_WHITE
            "90" -> options.textColor = AnsiCodes.GREY
        }
    }
}
