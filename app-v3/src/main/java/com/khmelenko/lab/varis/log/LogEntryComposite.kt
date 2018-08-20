package com.khmelenko.lab.varis.log

import java.util.ArrayList

internal class LogEntryComposite(val name: String?) : LogEntryComponent {
    private val logEntryComponents = ArrayList<LogEntryComponent>()

    fun append(logEntryComponent: LogEntryComponent) {
        logEntryComponents.add(logEntryComponent)
    }

    override fun toHtml(): String {
        var data = ""
        if (name == null) {
            data += "<body style=\"background-color:#222222;\">"
        }
        for (re in logEntryComponents) {
            data += re.toHtml()
        }
        if (name == null) {
            data += "</body>"
        }
        return data
    }
}
