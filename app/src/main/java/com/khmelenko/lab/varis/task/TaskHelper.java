package com.khmelenko.lab.varis.task;

import com.khmelenko.lab.varis.network.retrofit.github.GitHubRestClient;
import com.khmelenko.lab.varis.network.retrofit.raw.RawClient;
import com.khmelenko.lab.varis.network.retrofit.travis.TravisRestClient;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Helper class for Task
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class TaskHelper {

    private final TravisRestClient mTravisRestClient;
    private final GitHubRestClient mGitHubRestClient;
    private final RawClient mRawClient;
    private final EventBus mEventBus;

    @Inject
    public TaskHelper(TravisRestClient travisRestClient, GitHubRestClient gitHubRestClient,
                      RawClient rawClient, EventBus eventBus) {
        mTravisRestClient = travisRestClient;
        mGitHubRestClient = gitHubRestClient;
        mRawClient = rawClient;
        mEventBus = eventBus;
    }

    public TravisRestClient getTravisRestClient() {
        return mTravisRestClient;
    }

    public EventBus getEventBus() {
        return mEventBus;
    }

    public GitHubRestClient getGitHubRestClient() {
        return mGitHubRestClient;
    }

    public RawClient getRawClient() {
        return mRawClient;
    }
}
