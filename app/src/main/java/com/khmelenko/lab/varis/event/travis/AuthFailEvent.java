package com.khmelenko.lab.varis.event.travis;

import com.khmelenko.lab.varis.task.TaskError;

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
