package com.khmelenko.lab.varis.event.github;

import com.khmelenko.lab.varis.network.response.Authorization;

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
