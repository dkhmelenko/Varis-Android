package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.Branches;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Defines Branches task
 *
 * @author Dmytro Khmelenko
 */
public final class BranchesTask extends Task<Branches> {

    private long mRepoId;
    private String mRepoSlug;

    public BranchesTask(long repoId) {
        mRepoId = repoId;
    }

    public BranchesTask(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    @Override
    public Branches execute() throws TaskException {
        Branches branches;
        if (!TextUtils.isEmpty(mRepoSlug)) {
            branches = restClient().getApiService().getBranches(mRepoSlug);
        } else {
            branches = restClient().getApiService().getBranches(mRepoId);
        }
        return branches;
    }

    @Override
    public void onSuccess(Branches result) {
        BranchesLoadedEvent event = new BranchesLoadedEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
