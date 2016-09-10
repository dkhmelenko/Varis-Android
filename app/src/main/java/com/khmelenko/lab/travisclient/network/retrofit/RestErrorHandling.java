package com.khmelenko.lab.travisclient.network.retrofit;

import android.content.Context;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Custom error handler for Retrofit
 *
 * @author Dmytro Khmelenko
 */
public final class RestErrorHandling implements ErrorHandler {

    @Override
    public TaskException handleError(RetrofitError cause) {
        TaskError taskError = null;

        Context context = TravisApp.getAppContext();

        if (cause.isNetworkError()) {
            String msg = context.getString(R.string.error_network);
            taskError = new TaskError(TaskError.NETWORK_ERROR, msg);
        } else {
            if (cause.getResponse() == null) {
                String msg = context.getString(R.string.error_no_response);
                taskError = new TaskError(TaskError.NO_RESPONSE, msg);
            } else {
                int httpStatus = cause.getResponse().getStatus();
                if (httpStatus == 200) {
                    String msg = context.getString(R.string.error_parsing);
                    taskError = new TaskError(httpStatus, msg);
                } else {
                    taskError = new TaskError(httpStatus, cause.getResponse().getReason());
                }
                taskError.setResponse(cause.getResponse());
            }
        }

        return new TaskException(taskError);
    }
}
