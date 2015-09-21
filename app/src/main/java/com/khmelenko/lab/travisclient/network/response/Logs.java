package com.khmelenko.lab.travisclient.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Dao for logs
 *
 * @author Dmytro Khmelenko
 */
public final class Logs {

    @SerializedName("log")
    private Log mLog;

    public Log getLog() {
        return mLog;
    }

    public void setLog(Log log) {
        mLog = log;
    }
}
