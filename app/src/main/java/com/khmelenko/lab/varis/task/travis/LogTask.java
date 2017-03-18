package com.khmelenko.lab.varis.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.varis.event.travis.LogFailEvent;
import com.khmelenko.lab.varis.event.travis.LogLoadedEvent;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Task for getting log
 *
 * @author Dmytro Khmelenko
 */
public class LogTask extends Task<String> {

    private final String mAuth;
    private final long mJobId;

    /**
     * Constructor
     *
     * @param auth  Authorization header
     * @param jobId Job ID
     */
    public LogTask(String auth, long jobId) {
        mAuth = auth;
        mJobId = jobId;
    }

    @Override
    public String execute() throws TaskException {
        String redirectUrl = "";

        try {
            Response response;
            if (TextUtils.isEmpty(mAuth)) {
                response = rawClient().getApiService().getLog(String.valueOf(mJobId));
            } else {
                response = rawClient().getApiService().getLog(mAuth, String.valueOf(mJobId));
            }

            // in case of success just return the url. It means that the log file can be accessed
            // by this url already
            if (response.getStatus() == 200) {
                redirectUrl = response.getUrl();
            }

        } catch (TaskException exception) {
            Response response = exception.getTaskError().getResponse();
            for (Header header : response.getHeaders()) {
                if (header.getName().equals("Location")) {
                    redirectUrl = header.getValue();
                    break;
                }
            }

            boolean redirect = response.getStatus() == 307 && !TextUtils.isEmpty(redirectUrl);
            if (!redirect) {
                throw exception;
            }
        }
        return redirectUrl;
    }

    @Override
    public void onSuccess(String result) {
        LogLoadedEvent event = new LogLoadedEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LogFailEvent event = new LogFailEvent(error);
        eventBus().post(event);
    }
}
