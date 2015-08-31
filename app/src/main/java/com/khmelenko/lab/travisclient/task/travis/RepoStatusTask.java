package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.RepoStatusLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.RepoStatus;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Defines the Repository status task
 *
 * @author Dmytro Khmelenko
 */
public class RepoStatusTask extends Task<RepoStatus> {

    private long mRepoId;
    private String mRepoSlug;

    public RepoStatusTask(long repoId) {
        mRepoId = repoId;
    }

    public RepoStatusTask(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    @Override
    public RepoStatus execute() throws TaskException {
        RepoStatus builds;
        if (!TextUtils.isEmpty(mRepoSlug)) {
            builds = mRestClient.getApiService().getBuilds(mRepoSlug);
        } else {
            builds = mRestClient.getApiService().getBuilds(mRepoId);
        }
        return builds;
    }

    @Override
    public void onSuccess(RepoStatus result) {
        RepoStatusLoadedEvent event = new RepoStatusLoadedEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        mEventBus.post(event);
    }
}
