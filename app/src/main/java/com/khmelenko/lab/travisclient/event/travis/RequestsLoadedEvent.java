package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.network.response.Requests;

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
