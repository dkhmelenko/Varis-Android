package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.User;

/**
 * Event on loaded user information
 *
 * @author Dmytro Khmelenko
 */
public final class UserSuccessEvent {

    private final User mUser;

    public UserSuccessEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
