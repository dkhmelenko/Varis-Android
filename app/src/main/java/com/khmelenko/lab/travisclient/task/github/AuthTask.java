package com.khmelenko.lab.travisclient.task.github;

import com.khmelenko.lab.travisclient.event.github.AuthFailEvent;
import com.khmelenko.lab.travisclient.event.github.AuthSuccessEvent;
import com.khmelenko.lab.travisclient.network.dao.GithubAccessToken;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Defines authentication task
 *
 * @author Dmytro Khmelenko
 */
public class AuthTask extends Task<GithubAccessToken> {

    private final String mClientId;
    private final String mClientSecret;
    private final String mAccessCode;

    public AuthTask(String clientId, String clientSecret, String accessCode) {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mAccessCode = accessCode;
    }

    @Override
    public GithubAccessToken execute() throws TaskException {
        GithubAccessToken accessToken = mRestClient.getGithubApiService().getAccesToken(mClientId, mClientSecret, mAccessCode);
        return accessToken;
    }

    @Override
    public void onSuccess(GithubAccessToken result) {
        AuthSuccessEvent successEvent = new AuthSuccessEvent(result.getAccessToken());
        mEventBus.post(successEvent);
    }

    @Override
    public void onFail(TaskError error) {
        AuthFailEvent failEvent = new AuthFailEvent(error);
        mEventBus.post(failEvent);
    }
}
