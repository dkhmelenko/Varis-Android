package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.BuildsLoadedEvent;
import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.Build;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

import java.util.List;

/**
 * Defines the Build task
 *
 * @author Dmytro Khmelenko
 */
public class BuildsTask extends Task<List<Build>> {

    private long mRepoId;
    private String mRepoSlug;

    public BuildsTask(long repoId) {
        mRepoId = repoId;
    }

    public BuildsTask(String repoSlug) {
        mRepoSlug = repoSlug;
    }

    @Override
    public List<Build> execute() throws TaskException {
        List<Build> builds;
        if (!TextUtils.isEmpty(mRepoSlug)) {
            builds = mRestClient.getApiService().getBuilds(mRepoSlug);
        } else {
            builds = mRestClient.getApiService().getBuilds(mRepoId);
        }
        return builds;
    }

    @Override
    public void onSuccess(List<Build> result) {
        BuildsLoadedEvent event = new BuildsLoadedEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        mEventBus.post(event);
    }
}
