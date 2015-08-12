package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event of failed authentication
 *
 * @author Dmytro Khmelenko
 */
public final class AuthFailEvent {

    private final TaskError mTaskError;

    public AuthFailEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
