package com.khmelenko.lab.varis.log;

/**
 * Defines an interface for the log entry in build log
 */
public interface LogEntryComponent {

    /**
     * Transforms the entry to html format
     *
     * @return Html formatted string
     */
    String toHtml();
}
