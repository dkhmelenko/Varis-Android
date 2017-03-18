package com.khmelenko.lab.varis.task.travis;

import com.khmelenko.lab.varis.event.travis.AuthFailEvent;
import com.khmelenko.lab.varis.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.varis.network.request.AccessTokenRequest;
import com.khmelenko.lab.varis.network.response.AccessToken;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Authentication task for Travis
 *
 * @author Dmytro Khmelenko
 */
public class AuthTask extends Task<AccessToken> {

    private final String mGithubAccessToken;

    public AuthTask(String githubAccessToken) {
        mGithubAccessToken = githubAccessToken;
    }

    @Override
    public AccessToken execute() throws TaskException {
        AccessTokenRequest request = new AccessTokenRequest();
        request.setGithubToken(mGithubAccessToken);
        AccessToken accessToken = travisClient().getApiService().auth(request);
        return accessToken;
    }

    @Override
    public void onSuccess(AccessToken result) {
        AuthSuccessEvent successEvent = new AuthSuccessEvent(result.getAccessToken());
        eventBus().post(successEvent);
    }

    @Override
    public void onFail(TaskError error) {
        AuthFailEvent failEvent = new AuthFailEvent(error);
        eventBus().post(failEvent);
    }
}
