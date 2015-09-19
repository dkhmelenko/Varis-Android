package com.khmelenko.lab.travisclient.event.github;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event of failed authorization
 *
 * @author Dmytro Khmelenko
 */
public final class GithubAuthorizationFailEvent {

    private final TaskError mTaskError;

    public GithubAuthorizationFailEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
