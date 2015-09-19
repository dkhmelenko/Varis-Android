package com.khmelenko.lab.travisclient.task.github;

import com.khmelenko.lab.travisclient.event.github.CreateAuthorizationSuccessEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.travisclient.network.request.AuthorizationRequest;
import com.khmelenko.lab.travisclient.network.response.Authorization;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * The task for creation new authorization
 *
 * @author Dmytro Khmelenko
 */
public class CreateAuthorizationTask extends Task<Authorization> {

    private final String mBasicAuth;
    private final AuthorizationRequest mAuthorizationRequest;

    public CreateAuthorizationTask(String basicAuth, AuthorizationRequest authorizationRequest) {
        mBasicAuth = basicAuth;
        mAuthorizationRequest = authorizationRequest;
    }

    @Override
    public Authorization execute() throws TaskException {
        Authorization authorization = mRestClient.getGithubApiService()
                .createNewAuthorization(mBasicAuth, mAuthorizationRequest);
        return authorization;
    }

    @Override
    public void onSuccess(Authorization result) {
        CreateAuthorizationSuccessEvent event = new CreateAuthorizationSuccessEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        GithubAuthorizationFailEvent event = new GithubAuthorizationFailEvent(error);
        mEventBus.post(event);
    }
}
