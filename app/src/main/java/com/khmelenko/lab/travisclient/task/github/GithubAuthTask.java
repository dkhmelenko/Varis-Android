package com.khmelenko.lab.travisclient.task.github;

import com.khmelenko.lab.travisclient.event.github.GithubAuthFailEvent;
import com.khmelenko.lab.travisclient.event.github.GithubAuthSuccessEvent;
import com.khmelenko.lab.travisclient.network.response.GithubAccessToken;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Defines authentication task
 *
 * @author Dmytro Khmelenko
 */
public class GithubAuthTask extends Task<GithubAccessToken> {

    private final String mClientId;
    private final String mClientSecret;
    private final String mAccessCode;

    public GithubAuthTask(String clientId, String clientSecret, String accessCode) {
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
        GithubAuthSuccessEvent successEvent = new GithubAuthSuccessEvent(result.getAccessToken());
        mEventBus.post(successEvent);
    }

    @Override
    public void onFail(TaskError error) {
        GithubAuthFailEvent failEvent = new GithubAuthFailEvent(error);
        mEventBus.post(failEvent);
    }
}
