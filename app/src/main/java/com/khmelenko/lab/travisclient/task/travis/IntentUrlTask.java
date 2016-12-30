package com.khmelenko.lab.travisclient.task.travis;

import android.content.Context;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.TravisApp;
import com.khmelenko.lab.travisclient.event.travis.IntentUrlSuccessEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

import java.io.IOException;

import com.squareup.okhttp.Response;

/**
 * Intent URL task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class IntentUrlTask extends Task<String> {

    private final String mUrl;

    /**
     * Constructor
     *
     * @param url URL
     */
    public IntentUrlTask(String url) {
        mUrl = url;
    }

    @Override
    public String execute() throws TaskException {
        String redirectUrl = mUrl;

        try {
            Response response = rawClient().singleRequest(mUrl);
            if(response.isRedirect()) {
                redirectUrl = response.header("Location", "");
            }
        } catch (IOException e) {
            Context context = TravisApp.getAppContext();
            String msg = context.getString(R.string.error_network);
            TaskError taskError = new TaskError(TaskError.NETWORK_ERROR, msg);
            throw new TaskException(taskError);
        }
        return redirectUrl;
    }

    @Override
    public void onSuccess(String result) {
        IntentUrlSuccessEvent event = new IntentUrlSuccessEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
