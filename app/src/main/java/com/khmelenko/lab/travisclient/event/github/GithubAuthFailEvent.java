package com.khmelenko.lab.travisclient.event.github;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event of failed authentication
 *
 * @author Dmytro Khmelenko
 */
public final class GithubAuthFailEvent {

    private final TaskError mTaskError;

    public GithubAuthFailEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
