package com.khmelenko.lab.travisclient.event.github;

import com.khmelenko.lab.travisclient.network.response.Authorization;

/**
 * Event on successful authorization
 *
 * @author Dmytro Khmelenko
 */
public final class CreateAuthorizationSuccessEvent {

    private final Authorization mAuthorization;

    public CreateAuthorizationSuccessEvent(Authorization authorization) {
        mAuthorization = authorization;
    }

    public Authorization getAuthorization() {
        return mAuthorization;
    }
}
