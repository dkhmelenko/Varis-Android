package com.khmelenko.lab.varis.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.varis.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.varis.event.travis.RepoLoadedEvent;
import com.khmelenko.lab.varis.network.response.Repo;
import com.khmelenko.lab.varis.task.Task;
import com.khmelenko.lab.varis.task.TaskError;
import com.khmelenko.lab.varis.task.TaskException;

/**
 * Task for loading repositories
 *
 * @author Dmytro Khmelenko
 */
public class RepoTask extends Task<Repo> {

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
            repo = travisClient().getApiService().getRepo(mRepositorySlug);
        } else {
            repo = travisClient().getApiService().getRepo(mRepositoryId);
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
