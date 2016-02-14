package com.khmelenko.lab.travisclient.task;

import com.khmelenko.lab.travisclient.network.retrofit.RestClient;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Helper class for Task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class TaskHelper {

    private final RestClient mRestClient;
    private final EventBus mEventBus;

    @Inject
    public TaskHelper(RestClient restClient, EventBus eventBus) {
        mRestClient = restClient;
        mEventBus = eventBus;
    }

    public RestClient getRestClient() {
        return mRestClient;
    }

    public EventBus getEventBus() {
        return mEventBus;
    }
}
