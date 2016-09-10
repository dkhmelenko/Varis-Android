package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BuildHistoryFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.BuildHistoryLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Defines the Build history task
 *
 * @author Dmytro Khmelenko
 */
public final class BuildHistoryTask extends Task<BuildHistory> {

    private long mRepoId;
    private String mRepoSlug;

    public BuildHistoryTask(long repoId) {
        mRepoId = repoId;
    }

    public BuildHistoryTask(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    @Override
    public BuildHistory execute() throws TaskException {
        BuildHistory builds;
        if (!TextUtils.isEmpty(mRepoSlug)) {
            builds = travisClient().getApiService().getBuilds(mRepoSlug);
        } else {
            builds = travisClient().getApiService().getBuilds(mRepoId);
        }
        return builds;
    }

    @Override
    public void onSuccess(BuildHistory result) {
        BuildHistoryLoadedEvent event = new BuildHistoryLoadedEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        BuildHistoryFailedEvent event = new BuildHistoryFailedEvent(error);
        eventBus().post(event);
    }
}
