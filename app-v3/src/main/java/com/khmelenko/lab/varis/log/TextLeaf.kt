package com.khmelenko.lab.varis.log

internal class TextLeaf @JvmOverloads constructor(
        var text: String = "",
        var options: FormattingOptions = FormattingOptions.fromAnsiCodes()
) : LogEntryComponent {

    constructor(options: FormattingOptions) : this("", options)

    override fun toHtml(): String {
        var result = text.replace("\\r\\n?|\\n".toRegex(), "<br />")
        if (options.textColor != null) {
            result = "<font color='" + options.textColor + "'>" + result + "</font>"
        } else {
            result = "<font color='#FFF'>$result</font>"
        }
        if (options.isBold) {
            result = "<b>$result</b>"
        }
        if (options.isItalic) {
            result = "<i>$result</i>"
        }
        return result
    }

    override fun toString(): String {
        return "TextLeaf{" +
                "options=" + options +
                ", text='" + text + '\''.toString() +
                '}'.toString()
    }
}
