package com.khmelenko.lab.travisclient.event.travis;

/**
 * Event on successful authentication
 *
 * @author Dmytro Khmelenko
 */
public final class AuthSuccessEvent {

    private final String mAccessToken;

    public AuthSuccessEvent(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }
}
