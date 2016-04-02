package com.khmelenko.lab.travisclient.event.travis;

/**
 * Event on success execution of the IntentUrl task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class IntentUrlSuccessEvent {

    private final String mRedirectUrl;

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public IntentUrlSuccessEvent(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }
}
