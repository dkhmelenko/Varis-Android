package com.khmelenko.lab.travisclient.task.travis;

import com.khmelenko.lab.travisclient.event.travis.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.travis.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.network.request.AccessTokenRequest;
import com.khmelenko.lab.travisclient.network.response.AccessToken;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

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
        AccessToken accessToken = mRestClient.getApiService().auth(request);
        return accessToken;
    }

    @Override
    public void onSuccess(AccessToken result) {
        AuthSuccessEvent successEvent = new AuthSuccessEvent(result.getAccessToken());
        mEventBus.post(successEvent);
    }

    @Override
    public void onFail(TaskError error) {
        AuthFailEvent failEvent = new AuthFailEvent(error);
        mEventBus.post(failEvent);
    }
}
