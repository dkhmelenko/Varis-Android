package com.khmelenko.lab.travisclient.task.github;

import com.khmelenko.lab.travisclient.event.github.DeleteAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * The task for deletion authorization
 *
 * @author Dmytro Khmelenko
 */
public class DeleteAuthorizationTask extends Task<Void>{

    private final String mBasicAuth;

    public DeleteAuthorizationTask(String basicAuth) {
        mBasicAuth = basicAuth;
    }

    @Override
    public Void execute() throws TaskException {
        mRestClient.getGithubApiService().deleteAuthorization(mBasicAuth);
        return null;
    }

    @Override
    public void onSuccess(Void result) {
        DeleteAuthorizationSuccessEvent event = new DeleteAuthorizationSuccessEvent();
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        GithubAuthorizationFailEvent event = new GithubAuthorizationFailEvent(error);
        mEventBus.post(event);
    }
}
