package com.khmelenko.lab.varis.task.github;

import android.text.TextUtils;

import com.khmelenko.lab.varis.event.github.CreateAuthorizationSuccessEvent;
import com.khmelenko.lab.varis.event.github.GithubAuthorizationFailEvent;
import com.khmelenko.lab.varis.network.request.AuthorizationRequest;
import com.khmelenko.lab.varis.network.response.Authorization;
import com.khmelenko.lab.varis.network.retrofit.github.GithubApiService;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * The task for creation new authorization
 *
 * @author Dmytro Khmelenko
 */
public class CreateAuthorizationTask extends Task<Authorization> {

    private final String mBasicAuth;
    private final AuthorizationRequest mAuthorizationRequest;
    private String mTwoFactorCode;

    public CreateAuthorizationTask(String basicAuth, AuthorizationRequest authorizationRequest) {
        mBasicAuth = basicAuth;
        mAuthorizationRequest = authorizationRequest;
    }

    public CreateAuthorizationTask(String basicAuth, AuthorizationRequest authorizationRequest, String twoFactorCode) {
        mBasicAuth = basicAuth;
        mAuthorizationRequest = authorizationRequest;
        mTwoFactorCode = twoFactorCode;
    }

    @Override
    public Authorization execute() throws TaskException {

        Authorization authorization;
        try {
            if (!TextUtils.isEmpty(mTwoFactorCode)) {
                authorization = gitHubClient().getApiService().createNewAuthorization(mBasicAuth,
                        mTwoFactorCode, mAuthorizationRequest);
            } else {
                authorization = gitHubClient().getApiService()
                        .createNewAuthorization(mBasicAuth, mAuthorizationRequest);
            }
        } catch (TaskException error) {

            Response response = error.getTaskError().getResponse();
            if(response != null) {
                boolean twoFactorAuthRequired = false;
                for (Header header : response.getHeaders()) {
                    if (GithubApiService.TWO_FACTOR_HEADER.equals(header.getName())) {
                        twoFactorAuthRequired = true;
                        break;
                    }
                }
                if (response.getStatus() == 401 && twoFactorAuthRequired) {
                    TaskError taskError = new TaskError(TaskError.TWO_FACTOR_AUTH_REQUIRED, "");
                    throw new TaskException(taskError);
                } else {
                    throw error;
                }
            } else {
                throw error;
            }
        }

        return authorization;
    }

    @Override
    public void onSuccess(Authorization result) {
        CreateAuthorizationSuccessEvent event = new CreateAuthorizationSuccessEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        GithubAuthorizationFailEvent event = new GithubAuthorizationFailEvent(error);
        eventBus().post(event);
    }
}
