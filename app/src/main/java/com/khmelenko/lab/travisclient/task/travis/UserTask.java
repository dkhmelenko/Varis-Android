package com.khmelenko.lab.travisclient.task.travis;

import com.khmelenko.lab.travisclient.event.travis.LoadingFailedEvent;
import com.khmelenko.lab.travisclient.event.travis.UserSuccessEvent;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

/**
 * Task for fetching user data
 *
 * @author Dmytro Khmelenko
 */
public final class UserTask extends Task<User> {

    @Override
    public User execute() throws TaskException {
        User user = travisClient().getApiService().getUser();
        return user;
    }

    @Override
    public void onSuccess(User result) {
        UserSuccessEvent event = new UserSuccessEvent(result);
        eventBus().post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LoadingFailedEvent event = new LoadingFailedEvent(error);
        eventBus().post(event);
    }
}
