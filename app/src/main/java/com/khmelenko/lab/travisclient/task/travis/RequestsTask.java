package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BranchesLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RequestsLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.BuildHistory;
import com.khmelenko.lab.travisclient.network.response.Requests;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * @author Dmytro Khmelenko
 */
public final class RequestsTask extends Task<Requests> {

    private long mRepoId;
    private String mRepoSlug;

    public RequestsTask(long repoId) {
        mRepoId = repoId;
    }

    public RequestsTask(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    @Override
    public Requests execute() throws TaskException {
        Requests requests;
        BuildHistory buildHistory;
        if(!TextUtils.isEmpty(mRepoSlug)) {
            requests = mRestClient.getApiService().getRequests(mRepoSlug);
            buildHistory = mRestClient.getApiService().getBuilds(mRepoSlug);
            requests.setBuilds(buildHistory.getBuilds());
        } else {
            requests = mRestClient.getApiService().getRequests(mRepoId);
            buildHistory = mRestClient.getApiService().getBuilds(mRepoId);
            requests.setBuilds(buildHistory.getBuilds());
        }
        return requests;
    }

    @Override
    public void onSuccess(Requests result) {
        RequestsLoadedEvent event = new RequestsLoadedEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        mEventBus.post(event);
    }
}
