package com.khmelenko.lab.varis.log

/**
 * Defines possible options for formatting
 */
internal class FormattingOptions {

    var textColor: String? = null
    var background: String? = null
    var isBold: Boolean = false
    var isItalic: Boolean = false
    var isUnderline: Boolean = false

    override fun toString(): String {
        return "FormattingOptions{" +
                "textColor=" + textColor +
                ", background=" + background +
                ", bold=" + isBold +
                ", italic=" + isItalic +
                ", underline=" + isUnderline +
                '}'.toString()
    }

    companion object {

        fun fromAnsiCodes(vararg ansiStates: String): FormattingOptions {
            val options = FormattingOptions()
            for (ansiCode in ansiStates) {
                AnsiCodes.applyAnsiCode(options, ansiCode)
            }
            return options
        }
    }
}
