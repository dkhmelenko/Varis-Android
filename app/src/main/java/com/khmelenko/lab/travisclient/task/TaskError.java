package com.khmelenko.lab.travisclient.task;

import retrofit.client.Response;

/**
 * Defines the error for the Task
 *
 * @author Dmytro Khmelenko
 */
public final class TaskError {

    public static final int NETWORK_ERROR = -1;
    public static final int NO_RESPONSE = -2;
    public static final int TWO_FACTOR_AUTH_REQUIRED = -3;

    private final int mCode;
    private final String mMessage;
    private Response mResponse;

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

    public Response getResponse() {
        return mResponse;
    }

    public void setResponse(Response response) {
        mResponse = response;
    }
}
