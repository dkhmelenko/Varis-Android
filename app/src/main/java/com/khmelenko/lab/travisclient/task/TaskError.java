package com.khmelenko.lab.travisclient.task;

/**
 * Defines the error for the Task
 *
 * @author Dmytro Khmelenko
 */
public final class TaskError {

    private final int mCode;
    private final String mMessage;

    public TaskError(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
