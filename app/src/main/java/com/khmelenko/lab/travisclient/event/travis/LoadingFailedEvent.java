package com.khmelenko.lab.travisclient.event.travis;

import com.khmelenko.lab.travisclient.task.TaskError;

/**
 * Event on failed loading data
 *
 * @author Dmytro Khmelenko
 */
public final class LoadingFailedEvent {

    private final TaskError mTaskError;

    public LoadingFailedEvent(TaskError taskError) {
        mTaskError = taskError;
    }

    public TaskError getTaskError() {
        return mTaskError;
    }
}
