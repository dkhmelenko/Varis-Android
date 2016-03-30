package com.khmelenko.lab.travisclient.event.travis;

/**
 * Event on success execution of the IntentBuildDetails task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class IntentBuildDetailsSuccessEvent {

    private final String mRedirectUrl;

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public IntentBuildDetailsSuccessEvent(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }
}
