package com.khmelenko.lab.varis.log;

import java.util.ArrayList;
import java.util.List;

public class LogEntryComposite implements LogEntryComponent {

    private String name;
    private List<LogEntryComponent> logEntryComponents = new ArrayList<>();

    public LogEntryComposite(String name) {
        this.name = name;
    }

    void append(LogEntryComponent logEntryComponent) {
        logEntryComponents.add(logEntryComponent);
    }

    @Override
    public String toHtml() {
        String data = "";
        if (name == null) {
            data += "<body style=\"background-color:black;\">";
        }
        for (LogEntryComponent re : logEntryComponents) {
            data += re.toHtml();
        }
        if (name == null) {
            data += "</body>";
        }
        return data;
    }
}
