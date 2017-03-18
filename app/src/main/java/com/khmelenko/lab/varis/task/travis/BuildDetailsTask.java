package com.khmelenko.lab.varis.task.travis;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.khmelenko.lab.varis.event.travis.BuildDetailsLoadedEvent;
import com.khmelenko.lab.varis.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.varis.network.response.BuildDetails;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Task for getting build details
 *
 * @author Dmytro Khmelenko
 */
public class BuildDetailsTask extends Task<BuildDetails> {

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
            details = travisClient().getApiService().getBuild(mRepoSlug, mBuildId);
        } else {
            details = travisClient().getApiService().getBuild(mRepoId, mBuildId);
        }
        return details;
    }

    @Override
    public void onSuccess(BuildDetails result) {
        BuildDetailsLoadedEvent event = new BuildDetailsLoadedEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
