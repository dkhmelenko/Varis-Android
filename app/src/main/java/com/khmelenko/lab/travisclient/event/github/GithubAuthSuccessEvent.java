package com.khmelenko.lab.travisclient.event.github;

/**
 * Event on successful authentication
 *
 * @author Dmytro Khmelenko
 */
public final class GithubAuthSuccessEvent {

    private final String mAccessToken;

    public GithubAuthSuccessEvent(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }
}
