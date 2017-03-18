package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.network.response.Requests;

/**
 * Event on loaded requests
 *
 * @author Dmytro Khmelenko
 */
public final class RequestsLoadedEvent {

    private final Requests mRequests;

    public RequestsLoadedEvent(Requests requests) {
        mRequests = requests;
    }

    public Requests getRequests() {
        return mRequests;
    }
}
