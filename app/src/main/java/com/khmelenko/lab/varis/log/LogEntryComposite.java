package com.khmelenko.lab.varis.log;

import java.util.ArrayList;
import java.util.List;

final class LogEntryComposite implements LogEntryComponent {

    private String mName;
    private List<LogEntryComponent> mLogEntryComponents = new ArrayList<>();

    public LogEntryComposite(String name) {
        mName = name;
    }

    void append(LogEntryComponent logEntryComponent) {
        mLogEntryComponents.add(logEntryComponent);
    }

    @Override
    public String toHtml() {
        String data = "";
        if (mName == null) {
            data += "<body style=\"background-color:#222222;\">";
        }
        for (LogEntryComponent re : mLogEntryComponents) {
            data += re.toHtml();
        }
        if (mName == null) {
            data += "</body>";
        }
        return data;
    }
}
