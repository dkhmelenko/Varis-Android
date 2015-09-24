package com.khmelenko.lab.travisclient.task.travis;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BuildDetailsLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.BuildDetails;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Task for getting build details
 *
 * @author Dmytro Khmelenko
 */
public final class BuildDetailsTask extends Task<BuildDetails> {

    private String mRepoSlug;
    private long mRepoId;
    private final long mBuildId;

    public BuildDetailsTask(@NonNull String repoSlug, long buildId) {
        mRepoSlug = repoSlug;
        mBuildId = buildId;
    }

    public BuildDetailsTask(long repoId, long buildId) {
        mRepoId = repoId;
        mBuildId = buildId;
    }

    @Override
    public BuildDetails execute() throws TaskException {
        BuildDetails details;
        if (!TextUtils.isEmpty(mRepoSlug)) {
            details = mRestClient.getApiService().getBuild(mRepoSlug, mBuildId);
        } else {
            details = mRestClient.getApiService().getBuild(mRepoId, mBuildId);
        }
        return details;
    }

    @Override
    public void onSuccess(BuildDetails result) {
        BuildDetailsLoadedEvent event = new BuildDetailsLoadedEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        mEventBus.post(event);
    }
}
