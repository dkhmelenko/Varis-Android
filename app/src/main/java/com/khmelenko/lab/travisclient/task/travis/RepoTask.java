package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.RepoLoadedEvent;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Task for loading repositories
 *
 * @author Dmytro Khmelenko
 */
public final class RepoTask extends Task<Repo> {

    private long mRepositoryId;
    private String mRepositorySlug;

    public RepoTask(long repositoryId) {
        mRepositoryId = repositoryId;
    }

    public RepoTask(String repositorySlug) {
        mRepositorySlug = repositorySlug;
    }

    @Override
    public Repo execute() throws TaskException {
        Repo repo = null;
        if(!TextUtils.isEmpty(mRepositorySlug)) {
            repo = restClient().getApiService().getRepo(mRepositorySlug);
        } else {
            repo = restClient().getApiService().getRepo(mRepositoryId);
        }
        return repo;
    }

    @Override
    public void onSuccess(Repo result) {
        RepoLoadedEvent event = new RepoLoadedEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
