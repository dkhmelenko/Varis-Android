package com.khmelenko.lab.travisclient.event.travis;

/**
 * Event on loaded log data
 *
 * @author Dmytro Khmelenko
 */
public final class LogLoadedEvent {

    private final String mLogUrl;

    public LogLoadedEvent(String logUrl) {
        mLogUrl = logUrl;
    }

    public String getLogUrl() {
        return mLogUrl;
    }
}
