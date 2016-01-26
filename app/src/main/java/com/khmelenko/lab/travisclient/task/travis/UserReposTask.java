package com.khmelenko.lab.travisclient.task.travis;

import android.support.annotation.NonNull;

import com.khmelenko.lab.travisclient.event.travis.FindReposEvent;
import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.network.response.Repo;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

import java.util.List;

/**
 * Task for loading user related repositories
 *
 * @author Dmytro Khmelenko
 */
public final class UserReposTask extends Task<List<Repo>> {

    private final String mUserName;

    public UserReposTask(@NonNull String userName) {
        mUserName = userName;
    }

    @Override
    public List<Repo> execute() throws TaskException {
        List<Repo> repos = restClient().getApiService().getUserRepos(mUserName);
        return repos;
    }

    @Override
    public void onSuccess(List<Repo> result) {
        FindReposEvent event = new FindReposEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
