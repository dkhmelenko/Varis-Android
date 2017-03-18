package com.khmelenko.lab.varis.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.varis.event.travis.BranchesFailedEvent;
import com.khmelenko.lab.varis.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.varis.network.response.Branches;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Defines Branches task
 *
 * @author Dmytro Khmelenko
 */
public class BranchesTask extends Task<Branches> {

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
            branches = travisClient().getApiService().getBranches(mRepoSlug);
        } else {
            branches = travisClient().getApiService().getBranches(mRepoId);
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
        BranchesFailedEvent event = new BranchesFailedEvent(error);
        eventBus().post(event);
    }
}
